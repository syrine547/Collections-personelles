package Controllers;
import Service.LogService;
import Utils.DataSource;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class GenericCollectionController {

    @FXML
    private TableView<Map<String, Object>> tableElements; // Pour afficher les données
    @FXML
    private Button log;
    private String nomCollection;

    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
        initialiserUI();
    }

    private void initialiserUI() {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Récupérer les attributs actuels de la collection
            List<String> attributs = service.getAttributs();

            // Permettre l'édition des cellules dans la TableView
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
                    updateElementInDatabase(rowData); // Sauvegarder les modifications dans la base de données
                });
                tableElements.getColumns().add(column);
            }

            // Ajouter une colonne "Actions" avec des boutons
            TableColumn<Map<String, Object>, Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(col -> new TableCell<>() {
                private final Button deleteButton = new Button("Supprimer");
                {
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                    // Action du bouton "Supprimer"
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
            // Vérifier la présence de l'identifiant unique "id"
            if (item.containsKey("id") && item.get("id") instanceof Integer) {
                int id = (int) item.get("id");
                boolean success = service.supprimerElement(id, "id");
                if (success) {
                    tableElements.getItems().remove(item); // Mise à jour locale
                    showAlert("Succès", "Élément supprimé avec succès !");
                    logAction("Suppression d'un élément", getCollectionId()); // Journalisation
                } else {
                    showAlert("Erreur", "Impossible de supprimer l'élément.");
                }
            } else {
                showAlert("Erreur", "Élément invalide ou identifiant manquant.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de l'élément : " + e.getMessage());
        }
    }

    private void updateElementInDatabase(Map<String, Object> rowData) {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Vérifier la présence de l'identifiant unique "id"
            if (rowData.containsKey("id") && rowData.get("id") instanceof Integer) {
                int id = (int) rowData.get("id");
                boolean success = service.updateElement(id, "id", rowData);
                if (success) {
                    logAction("Suppression d'un élément", getCollectionId()); // Journalisation
                } else {
                    showAlert("Erreur", "Impossible de mettre à jour l'élément.");
                }
            } else {
                showAlert("Erreur", "Élément invalide ou identifiant manquant.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la mise à jour de l'élément : " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterElement() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un élément");
        dialog.setHeaderText("Ajoutez un nouvel élément à la collection : " + nomCollection);

        // Configurer les champs d'entrée
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        List<TextField> textFields = new ArrayList<>();
        List<String> attributsSansIDEtDate = new ArrayList<>();

        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Récupérer les attributs et exclure 'id' et 'dateAjout'
            List<String> attributs = service.getAttributs();
            for (String attribut : attributs) {
                if (!attribut.equalsIgnoreCase("id") && !attribut.equalsIgnoreCase("dateAjout")) {
                    attributsSansIDEtDate.add(attribut);

                    Label label = new Label(attribut + ":");
                    TextField textField = new TextField();
                    textField.setPromptText("Valeur");

                    textFields.add(textField);
                    grid.add(label, 0, attributsSansIDEtDate.size() - 1); // Ajouter le label
                    grid.add(textField, 1, attributsSansIDEtDate.size() - 1); // Ajouter le champ de saisie
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des attributs : " + e.getMessage());
            return;
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Map<String, String> newElement = new HashMap<>();
                for (int i = 0; i < attributsSansIDEtDate.size(); i++) {
                    newElement.put(attributsSansIDEtDate.get(i), textFields.get(i).getText());
                }
                return newElement;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(values -> {
            try (ServiceCollection service = new ServiceCollection(nomCollection)) {
                Map<String, Object> element = new HashMap<>(values);

                // Ajouter l'élément dans la base
                if (service.ajouterElement(element)) {
                    showAlert("Succès", "Élément ajouté avec succès !");
                    refreshTable(); // Rafraîchir les données pour inclure le nouvel élément
                    logAction("Suppression d'un élément", getCollectionId()); // Journalisation
                } else {
                    showAlert("Erreur", "Impossible d'ajouter l'élément.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout de l'élément : " + e.getMessage());
            }
        });
    }

    private void refreshTable() {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Récupérer les données mises à jour depuis la base de données
            List<Map<String, Object>> updatedData = service.readAll();

            // Effacer les anciennes données de la TableView
            tableElements.getItems().clear();

            // Ajouter les nouvelles données
            tableElements.getItems().addAll(updatedData);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du rafraîchissement de la table : " + e.getMessage());
        }
    }

    @FXML
    private void handleRetourDashboard() {
        try {
            // Récupérer l'ID utilisateur actuellement connecté
            int userId = AuthController.currentUserId;

            // Charger le FXML du Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du Dashboard et passer l'ID utilisateur
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserId(userId);  // Passe l'ID utilisateur au contrôleur du Dashboard
            }

            // Mettre à jour la scène avec le Dashboard
            Stage currentStage = (Stage) tableElements.getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard : " + e.getMessage());

        }
    }

    @FXML
    private void handleaffichelog() {
        try {
            Stage currentStage = (Stage) tableElements.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/journalisation.fxml"));
            if (getClass().getResource("/journalisation.fxml") == null) {
                throw new IOException("Fichier journalisation.fxml introuvable !"); }
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Journal");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de naviguer vers le journal. Détails : " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert("Erreur", "Le bouton 'log' est null. Vérifiez son ID dans le FXML.");
        }
    }

    private void logAction(String action, Integer collectionId) {
        int userId = getCurrentUserId(); // Récupérer l'ID de l'utilisateur actuel
        LogService.enregistrerAction(userId, action, collectionId); // Enregistrer l'action
    }

    private Integer getCurrentUserId() {
        // Remplacez ceci par la méthode réelle pour obtenir l'ID de l'utilisateur actuel
        return AuthController.currentUserId; // Exemple : utilisateur connecté
    }

    private Integer getCollectionId() {
        // Remplacez ceci par la méthode réelle pour obtenir l'ID de la collection actuelle
        return 1; // Exemple : collection fictive
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
