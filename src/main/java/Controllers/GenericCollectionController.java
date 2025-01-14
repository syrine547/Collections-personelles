package Controllers;

import Service.ServiceCollection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javafx.scene.control.Alert;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.util.Map;

public class GenericCollectionController {

    @FXML
    private TableView<Map<String, Object>> tableElements; // Pour afficher les données

    private String nomCollection;

    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
        initialiserUI();
    }

    private void initialiserUI() {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            List<String> attributs = service.getAttributs();

            tableElements.setEditable(true);

            // Configurer dynamiquement les colonnes du tableau
            tableElements.getColumns().clear();
            for (String attribut : attributs) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(attribut);
                column.setCellValueFactory(data -> new SimpleStringProperty(
                        data.getValue().getOrDefault(attribut, "").toString()
                ));
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setOnEditCommit(event -> {
                    // Mettre à jour la valeur dans les données
                    Map<String, Object> rowData = event.getRowValue();
                    rowData.put(attribut, event.getNewValue());
                    updateElementInDatabase(rowData); // Sauvegarder les modifications
                });
                tableElements.getColumns().add(column);
            }

            // Ajouter une colonne "Actions" avec des boutons
            TableColumn<Map<String, Object>, Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(col -> new TableCell<>() {
                private final Button deleteButton = new Button("Supprimer");

                {
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                    deleteButton.setOnAction(event -> {
                        Map<String, Object> currentItem = getTableView().getItems().get(getIndex());
                        handleDeleteElement(currentItem);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttons = new HBox(5, deleteButton);
                        buttons.setAlignment(Pos.CENTER);
                        setGraphic(buttons);
                    }
                }
            });

            tableElements.getColumns().add(actionsColumn);

            // Charger les données dans le tableau
            tableElements.getItems().clear();
            List<Map<String, Object>> data = service.readAll();
            tableElements.getItems().addAll(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation de la collection : " + e.getMessage());
        }
    }

    private void handleDeleteElement(Map<String, Object> item) {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Supposons que chaque élément a un identifiant unique dans la colonne "id"
            int id = (int) item.get("id");
            boolean success = service.supprimerElement(id, "id");
            if (success) {
                tableElements.getItems().remove(item);
                showAlert("Succès", "Élément supprimé avec succès !");
            } else {
                showAlert("Erreur", "Impossible de supprimer l'élément.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de l'élément : " + e.getMessage());
        }
    }

    private void updateElementInDatabase(Map<String, Object> rowData) {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            int id = (int) rowData.get("id");
            boolean success = service.updateElement(id, "id", rowData);
            if (success) {
                showAlert("Succès", "Élément mis à jour avec succès !");
            } else {
                showAlert("Erreur", "Impossible de mettre à jour l'élément.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la mise à jour de l'élément : " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterElement() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter un élément");
        dialog.setHeaderText("Ajouter un nouvel élément à la collection : " + nomCollection);
        dialog.setContentText("Entrez les valeurs (séparées par des virgules) :");

        dialog.showAndWait().ifPresent(values -> {
            try (ServiceCollection service = new ServiceCollection(nomCollection)) {
                String[] valeurs = values.split(",");
                List<String> attributs = service.getAttributs();

                // Associer chaque valeur à un attribut
                Map<String, Object> element = new HashMap<>();
                for (int i = 0; i < attributs.size() && i < valeurs.length; i++) {
                    element.put(attributs.get(i), valeurs[i]);
                }

                // Ajouter l'élément dans la base
                if (service.ajouterElement(element)) {
                    showAlert("Succès", "Élément ajouté avec succès !");
                    tableElements.getItems().add(element); // Mise à jour locale
                } else {
                    showAlert("Erreur", "Impossible d'ajouter l'élément.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout de l'élément : " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleRetourDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
