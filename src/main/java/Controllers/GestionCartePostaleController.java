package Controllers;

import Entity.CartePostale;
import Entity.Livre;
import Service.ServiceCartePostale;
import Utils.DataSource;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestionCartePostaleController {

    @FXML private TextField fieldTitreCartePostale;
    @FXML private TextField fieldQuantite;

    @FXML private TableView<CartePostale> tableCartePostale;
    @FXML private TableColumn<CartePostale, String> colTitreCartePostale;
    @FXML private TableColumn<CartePostale, Integer> colQuantite;

    private ObservableList<CartePostale> cartesPostales = FXCollections.observableArrayList();
    private ServiceCartePostale service = new ServiceCartePostale();

    private void chargerCartePostaleDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idCartePostale, titreCartePostale, quantité FROM CartePostale";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idCartePostale");
                String titre = resultSet.getString("titreCartePostale");
                int quantite = resultSet.getInt("quantité");

                // Crée un objet Carte Postale et l'ajoute à la liste observable
                CartePostale cartePostale = new CartePostale(id, titre, quantite);
                cartePostale.setQuantite(quantite); // Définir la quantité si elle n'est pas dans le constructeur
                cartesPostales.add(cartePostale);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche le stacktrace pour le débogage
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        colTitreCartePostale.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitreCartePostale()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        chargerCartePostaleDepuisBD();
        tableCartePostale.setItems(cartesPostales);
    }

    @FXML
    private void handleAjouterCartePostale(ActionEvent event) {
        try {
            String titre = fieldTitreCartePostale.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            CartePostale carte = new CartePostale(0, titre, quantite);
            service.ajouterCartePostale(carte);
            cartesPostales.add(carte);
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter la carte postale !");
        }
    }

    @FXML
    private void handleModifierCartePostale(ActionEvent event) {
        CartePostale selectedCarte = tableCartePostale.getSelectionModel().getSelectedItem();
        if (selectedCarte != null) {
            try {
                selectedCarte.setTitreCartePostale(fieldTitreCartePostale.getText());
                selectedCarte.setQuantite(Integer.parseInt(fieldQuantite.getText()));
                service.updateCartePostale(selectedCarte);
                tableCartePostale.refresh();
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier la carte postale !");
            }
        }
    }

    @FXML
    private void handleSupprimerCartePostale(ActionEvent event) {
        CartePostale selectedCarte = tableCartePostale.getSelectionModel().getSelectedItem();
        if (selectedCarte != null) {
            try {
                service.supprimerCartePostale(selectedCarte);
                cartesPostales.remove(selectedCarte);
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer la carte postale !");
            }
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
