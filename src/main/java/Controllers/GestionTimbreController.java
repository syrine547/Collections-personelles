package Controllers;

import Entity.Timbre;
import Service.ServiceTimbre;
import Utils.DataSource;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestionTimbreController {

    // Champs de saisie
    @FXML private TextField fieldNomTimbre;
    @FXML private TextField fieldQuantite;

    // TableView et colonnes
    @FXML private TableView<Timbre> tableTimbres;
    @FXML private TableColumn<Timbre, String> colNomTimbre;
    @FXML private TableColumn<Timbre, Integer> colQuantite;

    // Liste observable
    private ObservableList<Timbre> timbres = FXCollections.observableArrayList();

    // Instance du service
    private ServiceTimbre service = new ServiceTimbre();

    private void chargerTimbresDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idTimbre, nomTimbre, quantité FROM Timbres";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idTimbre");
                String nom = resultSet.getString("nomTimbre");
                int quantite = resultSet.getInt("quantité");

                Timbre timbre = new Timbre(id, nom, quantite);
                timbres.add(timbre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colNomTimbre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomTimbre()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Charger les données depuis la base de données
        chargerTimbresDepuisBD();

        // Associer les données à la TableView
        tableTimbres.setItems(timbres);
    }

    @FXML
    private void handleAjouterTimbre(ActionEvent event) {
        try {
            String nom = fieldNomTimbre.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            Timbre timbre = new Timbre(0, nom, quantite);

            // Ajouter à la base de données via le service
            boolean isAdded = service.ajouterTimbre(timbre);

            if (isAdded) {
                timbres.add(timbre);
                clearFields();
                showAlert("Succès", "Le timbre a été ajouté avec succès.");
            } else {
                showAlert("Erreur", "Le timbre n'a pas pu être ajouté à la base de données.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Erreur lors de l'ajout du timbre : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierTimbre(ActionEvent event) {
        Timbre selectedTimbre = tableTimbres.getSelectionModel().getSelectedItem();
        if (selectedTimbre != null) {
            try {
                selectedTimbre.setNomTimbre(fieldNomTimbre.getText());
                selectedTimbre.setQuantite(Integer.parseInt(fieldQuantite.getText()));

                boolean isUpdated = service.updateTimbre(selectedTimbre);

                if (isUpdated) {
                    tableTimbres.refresh();
                    clearFields();
                    showAlert("Succès", "Le timbre a été modifié avec succès.");
                } else {
                    showAlert("Erreur", "Le timbre n'a pas pu être modifié.");
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la modification du timbre : " + e.getMessage());
            }
        } else {
            showAlert("Aucun timbre sélectionné", "Veuillez sélectionner un timbre à modifier.");
        }
    }

    @FXML
    private void handleSupprimerTimbre(ActionEvent event) {
        Timbre selectedTimbre = tableTimbres.getSelectionModel().getSelectedItem();
        if (selectedTimbre != null) {
            try {
                boolean isDeleted = service.supprimerTimbre(selectedTimbre);

                if (isDeleted) {
                    timbres.remove(selectedTimbre);
                    clearFields();
                    showAlert("Succès", "Le timbre a été supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Le timbre n'a pas pu être supprimé.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la suppression du timbre : " + e.getMessage());
            }
        } else {
            showAlert("Aucun timbre sélectionné", "Veuillez sélectionner un timbre à supprimer.");
        }
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Stage stage = (Stage) tableTimbres.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard.");
        }
    }

    private void clearFields() {
        fieldNomTimbre.clear();
        fieldQuantite.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
