package Controllers;

import Entity.CartePostale;
import Service.ServiceCartePostale;
import Service.ServicePieceMonnaie;
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

public class GestionCartePostaleController {

    // Champs du formulaire
    @FXML private TextField fieldTitreCartePostale;
    @FXML private TextField fieldQuantite;

    // TableView et colonnes
    @FXML private TableView<CartePostale> tableCartePostale;
    @FXML private TableColumn<CartePostale, String> colTitreCartePostale;
    @FXML private TableColumn<CartePostale, Integer> colQuantite;

    // Liste observable pour les cartes postales
    private ObservableList<CartePostale> cartesPostales = FXCollections.observableArrayList();

    // Charger les cartes postales depuis la base de données
    private void chargerCartePostaleDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idCartePostale, titreCartePostale, quantité FROM CartePostale";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idCartePostale");
                String titre = resultSet.getString("titreCartePostale");
                int quantite = resultSet.getInt("quantité");

                // Crée un objet CartePostale et l'ajoute à la liste observable
                CartePostale cartePostale = new CartePostale(id, titre, quantite);
                cartesPostales.add(cartePostale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colTitreCartePostale.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitreCartePostale()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Charger les cartes postales existantes depuis la base de données
        chargerCartePostaleDepuisBD();

        // Associer les données à la TableView
        tableCartePostale.setItems(cartesPostales);
    }

    @FXML
    private void handleAjouterCartePostale(ActionEvent event) {
        try {
            String titre = fieldTitreCartePostale.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            CartePostale cartePostale = new CartePostale(0, titre, quantite);

            // Ajouter la carte postale dans la base de données
            ServiceCartePostale serviceCartePostale = new ServiceCartePostale();
            boolean isAdded = serviceCartePostale.ajouterCartePostale(cartePostale);

            if (isAdded) {
                cartesPostales.add(cartePostale);
                clearFields();
                showAlert("Succès", "La carte postale a été ajoutée avec succès.");
            } else {
                showAlert("Erreur", "La carte postale n'a pas pu être ajoutée.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer une quantité valide.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Erreur lors de l'ajout de la carte postale : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierCartePostale(ActionEvent event) {
        CartePostale selectedCartePostale = tableCartePostale.getSelectionModel().getSelectedItem();
        if (selectedCartePostale != null) {
            try {
                selectedCartePostale.setTitreCartePostale(fieldTitreCartePostale.getText());
                selectedCartePostale.setQuantite(Integer.parseInt(fieldQuantite.getText()));

                // Mettre à jour la carte postale dans la base de données
                ServiceCartePostale serviceCartePostale = new ServiceCartePostale();
                boolean isUpdated = serviceCartePostale.updateCartePostale(selectedCartePostale);

                if (isUpdated) {
                    tableCartePostale.refresh();
                    clearFields();
                    showAlert("Succès", "La carte postale a été modifiée avec succès.");
                } else {
                    showAlert("Erreur", "La carte postale n'a pas pu être modifiée.");
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer une quantité valide.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la mise à jour de la carte postale : " + e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une carte postale à modifier.");
        }
    }

    @FXML
    private void handleSupprimerCartePostale(ActionEvent event) {
        CartePostale selectedCartePostale = tableCartePostale.getSelectionModel().getSelectedItem();
        if (selectedCartePostale != null) {
            try {
                // Supprimer la carte postale de la base de données
                ServiceCartePostale serviceCartePostale = new ServiceCartePostale();
                boolean isDeleted = serviceCartePostale.supprimerCartePostale(selectedCartePostale);

                if (isDeleted) {
                    cartesPostales.remove(selectedCartePostale);
                    clearFields();
                    showAlert("Succès", "La carte postale a été supprimée avec succès.");
                } else {
                    showAlert("Erreur", "La carte postale n'a pas pu être supprimée.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la suppression de la carte postale : " + e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une carte postale à supprimer.");
        }
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Stage stage = (Stage) tableCartePostale.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard.");
        }
    }

    private void clearFields() {
        fieldTitreCartePostale.clear();
        fieldQuantite.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
