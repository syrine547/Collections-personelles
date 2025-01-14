package Controllers;

import Service.ServiceCollection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import java.util.List;

public class GenericCollectionController {

    @FXML
    private VBox vboxChamps; // Pour les champs dynamiques
    @FXML
    private TableView<Map<String, Object>> tableElements; // Pour afficher les données

    private String nomCollection;

    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
        initialiserUI();
    }

    private void initialiserUI() {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Récupérer les attributs de la collection
            List<String> attributs = service.getAttributs();

            // Ajouter des champs dynamiques pour chaque attribut
            vboxChamps.getChildren().clear(); // Nettoyer les champs existants
            for (String attribut : attributs) {
                Label label = new Label(attribut + ":");
                TextField textField = new TextField();
                textField.setId(attribut); // Utilisé pour identifier chaque champ
                vboxChamps.getChildren().addAll(label, textField);
            }

            // Configurer dynamiquement le tableau
            tableElements.getColumns().clear();
            for (String attribut : attributs) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(attribut);
                column.setCellValueFactory(data -> new SimpleStringProperty(
                        data.getValue().getOrDefault(attribut, "").toString()
                ));
                tableElements.getColumns().add(column);
            }

            // Charger les données dans le tableau
            tableElements.getItems().clear();
            List<Map<String, Object>> data = service.readAll();
            tableElements.getItems().addAll(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation de la collection : " + e.getMessage());
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
        // Fermer la fenêtre actuelle
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

        //Stage stage = (Stage) vboxChamps.getScene().getWindow();
        //stage.close();
    }

    public void afficherCollection(String nomCollection) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCollection.fxml"));
            Parent root = loader.load();

            GenericCollectionController controller = loader.getController();
            controller.setNomCollection(nomCollection);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion de la collection : " + nomCollection);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue gestionCollection.fxml : " + e.getMessage());
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
