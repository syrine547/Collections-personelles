package Controllers;

import Service.ServiceCollection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ModifierCollectionController {

    @FXML
    private TextField fieldNomCollection;
    @FXML
    private TextField fieldObjectifTotal;
    @FXML
    private TableView<Map<String, String>> tableAttributs;
    @FXML
    private TableColumn<Map<String, String>, String> colNomAttribut;
    @FXML
    private TableColumn<Map<String, String>, String> colTypeAttribut;

    private String ancienNomCollection;

    private ObservableList<Map<String, String>> attributs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNomAttribut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("nom")));
        colTypeAttribut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("type")));

        // Rendre les colonnes éditables
        colNomAttribut.setCellFactory(TextFieldTableCell.forTableColumn());
        colTypeAttribut.setCellFactory(ComboBoxTableCell.forTableColumn("VARCHAR(255)", "INT", "DATE", "FLOAT"));

        // Mise à jour des données lors de l'édition
        colNomAttribut.setOnEditCommit(event -> {
            Map<String, String> attribut = event.getRowValue();
            attribut.put("nom", event.getNewValue());
        });

        colTypeAttribut.setOnEditCommit(event -> {
            Map<String, String> attribut = event.getRowValue();
            attribut.put("type", event.getNewValue());
        });

        tableAttributs.setItems(attributs);
    }

    public void setCollectionDetails(String nomCollection, int objectifTotal, ObservableList<Map<String, String>> attributsInitial) {
        this.ancienNomCollection = nomCollection;
        fieldNomCollection.setText(nomCollection);
        fieldObjectifTotal.setText(String.valueOf(objectifTotal));
        attributs.setAll(attributsInitial);
    }

    @FXML
    private void handleAjouterAttribut() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Attribut");

        // Créer les champs d'entrée
        TextField fieldNom = new TextField();
        fieldNom.setPromptText("Nom de l'attribut");

        ComboBox<String> comboType = new ComboBox<>();
        comboType.getItems().addAll("VARCHAR(255)", "INT", "DATE", "FLOAT");
        comboType.setValue("VARCHAR(255)"); // Type par défaut

        VBox content = new VBox(10, new Label("Nom de l'attribut :"), fieldNom,
                new Label("Type de l'attribut :"), comboType);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Map<String, String> nouvelAttribut = new HashMap<>();
                nouvelAttribut.put("nom", fieldNom.getText());
                nouvelAttribut.put("type", comboType.getValue());
                return nouvelAttribut;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nouvelAttribut -> {
            if (!nouvelAttribut.get("nom").isEmpty()) {
                attributs.add(nouvelAttribut);
            }
        });
    }

    @FXML
    private void handleSupprimerAttribut() {
        Map<String, String> selected = tableAttributs.getSelectionModel().getSelectedItem();
        if (selected != null) {
            attributs.remove(selected);
        }
    }

    @FXML
    private void handleEnregistrerModifications() {
        String nouveauNom = fieldNomCollection.getText();
        int nouvelObjectif = Integer.parseInt(fieldObjectifTotal.getText());

        try (ServiceCollection service = new ServiceCollection(ancienNomCollection)) {
            // Renommer la collection si le nom a changé
            if (!ancienNomCollection.equals(nouveauNom)) {
                service.renommerCollection(ancienNomCollection, nouveauNom);
            }

            // Mettre à jour l'objectif
            service.mettreAJourObjectif(nouveauNom, nouvelObjectif);

            // Synchroniser les attributs avec la base de données
            service.synchroniserAttributs(nouveauNom, attributs);

            showAlert("Succès", "Les modifications ont été enregistrées avec succès !");
            ((Stage) fieldNomCollection.getScene().getWindow()).close(); // Fermer la fenêtre
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'enregistrement des modifications : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
