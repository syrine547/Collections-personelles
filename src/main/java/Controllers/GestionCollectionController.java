package Controllers;

import Service.ServiceCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class GestionCollectionController {

    @FXML
    private VBox vboxChamps;
    @FXML
    private TableView<Map<String, Object>> tableElements;

    private List<String> attributs; // Liste des attributs de la collection
    private Map<String, TextField> champsTextFields; // TextFields associés à chaque attribut
    private ObservableList<Map<String, Object>> elements; // Données de la collection

    public void setAttributs(List<String> attributs) {
        this.attributs = attributs;
        this.champsTextFields = new HashMap<>();
        this.elements = FXCollections.observableArrayList();

        // Créer dynamiquement les champs d'entrée et colonnes du tableau
        createDynamicFields();
        createDynamicColumns();
    }

    private String nomCollection; // Le nom de la collection à gérer
    @FXML
    private TableView<Map<String, Object>> tableView; // TableView pour afficher les données
    @FXML
    private TableColumn<Map<String, Object>, String> nomColonne; // Exemple de colonne

    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
    }

    @FXML
    public void initialize() {
        if (nomCollection != null) {
            try {
                ServiceCollection service = new ServiceCollection(nomCollection);
                List<Map<String, Object>> elements = service.readAll();

                // Configuration des colonnes du TableView dynamiquement
                if (!elements.isEmpty()) {
                    Map<String, Object> premierElement = elements.get(0);
                    for (String columnName : premierElement.keySet()) {
                        TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
                        column.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(columnName)));
                        tableView.getColumns().add(column);
                    }
                }

                ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList(elements);
                tableView.setItems(observableList);
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'erreur (afficher un message à l'utilisateur, etc.)
            }
        }
    }

    private void createDynamicFields() {
        vboxChamps.getChildren().clear();
        for (String attribut : attributs) {
            TextField textField = new TextField();
            textField.setPromptText(attribut);
            textField.setStyle("-fx-pref-width: 200; -fx-background-color: #bdc3c7;");
            champsTextFields.put(attribut, textField);
            vboxChamps.getChildren().add(textField);
        }

        // Ajouter les boutons d'actions
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAjouter.setOnAction(event -> handleAjouterElement());

        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnModifier.setOnAction(event -> handleModifierElement());

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnSupprimer.setOnAction(event -> handleSupprimerElement());

        vboxChamps.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer);
    }

    private void createDynamicColumns() {
        tableElements.getColumns().clear();
        for (String attribut : attributs) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(attribut);
            column.setCellValueFactory(data -> {
                Object value = data.getValue().get(attribut);
                return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
            });
            column.setPrefWidth(200);
            tableElements.getColumns().add(column);
        }
        tableElements.setItems(elements);
    }

    @FXML
    private void handleAjouterElement() {
        Map<String, Object> element = new HashMap<>();
        for (String attribut : attributs) {
            TextField textField = champsTextFields.get(attribut);
            if (textField != null) {
                element.put(attribut, textField.getText());
            }
        }
        elements.add(element);
        clearFields();
    }

    @FXML
    private void handleModifierElement() {
        Map<String, Object> selected = tableElements.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String attribut : attributs) {
                TextField textField = champsTextFields.get(attribut);
                if (textField != null) {
                    selected.put(attribut, textField.getText());
                }
            }
            tableElements.refresh();
            clearFields();
        } else {
            showAlert("Erreur", "Veuillez sélectionner un élément à modifier.");
        }
    }

    @FXML
    private void handleSupprimerElement() {
        Map<String, Object> selected = tableElements.getSelectionModel().getSelectedItem();
        if (selected != null) {
            elements.remove(selected);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un élément à supprimer.");
        }
    }

    @FXML
    private void handleRetourDashboard() {
        // Naviguer vers le Dashboard
    }

    private void clearFields() {
        for (TextField textField : champsTextFields.values()) {
            textField.clear();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
