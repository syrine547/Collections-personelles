package Controllers;

import Service.ServiceCollection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx. geometry. Insets;
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

        // Appliquer les modifications localement
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
        // Créer une boîte de dialogue personnalisée
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Attribut");
        dialog.setHeaderText("Ajoutez un nouvel attribut");

        // Boutons OK et Annuler
        ButtonType ajouterButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButtonType, ButtonType.CANCEL);

        // Contenu de la boîte de dialogue
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom de l'attribut");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("VARCHAR(255)", "INT", "DATE", "FLOAT");
        typeComboBox.setValue("VARCHAR(255)"); // Type par défaut

        grid.add(new Label("Nom de l'attribut:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Type de l'attribut:"), 0, 1);
        grid.add(typeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Récupérer les résultats
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButtonType) {
                Map<String, String> attribut = new HashMap<>();
                attribut.put("nom", nomField.getText());
                attribut.put("type", typeComboBox.getValue());
                return attribut;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nouvelAttribut -> {
            // Ajouter l'attribut localement
            attributs.add(nouvelAttribut);
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
            // Renommer la collection si nécessaire
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
