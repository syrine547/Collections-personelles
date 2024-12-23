package Controllers;

import Entity.Livre;
import Entity.Timbre;
import Service.ServiceTimbre;
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

public class GestionTimbreController {

    @FXML private TextField fieldNomTimbre;
    @FXML private TextField fieldQuantite;

    @FXML private TableView<Timbre> tableTimbres;
    @FXML private TableColumn<Timbre, String> colNomTimbre;
    @FXML private TableColumn<Timbre, Integer> colQuantite;

    private ObservableList<Timbre> timbres = FXCollections.observableArrayList();
    private ServiceTimbre service = new ServiceTimbre();

    private void chargerTimbreDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idTimbre, nomTimbre, quantité FROM Timbres";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idTimbre");
                String nom = resultSet.getString("nomTimbre");
                int quantite = resultSet.getInt("quantité");

                // Crée un objet Livre et l'ajoute à la liste observable
                Timbre timbre = new Timbre(nom);
                timbre.setQuantite(quantite);
                timbres.add(timbre);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche le stacktrace pour le débogage
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }
    @FXML
    public void initialize() {
        colNomTimbre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomTimbre()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        chargerTimbreDepuisBD();
        tableTimbres.setItems(timbres);
    }

    @FXML
    private void handleAjouterTimbre(ActionEvent event) {
        try {
            String nom = fieldNomTimbre.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            Timbre timbre = new Timbre(0, nom, quantite);
            service.ajouterTimbre(timbre);
            timbres.add(timbre);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter le timbre !");
        }
    }

    @FXML
    private void handleModifierTimbre(ActionEvent event) {
        Timbre selectedTimbre = tableTimbres.getSelectionModel().getSelectedItem();
        if (selectedTimbre != null) {
            try {
                selectedTimbre.setNomTimbre(fieldNomTimbre.getText());
                selectedTimbre.setQuantite(Integer.parseInt(fieldQuantite.getText()));
                service.updateTimbre(selectedTimbre);
                tableTimbres.refresh();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier le timbre !");
            }
        }
    }

    @FXML
    private void handleSupprimerTimbre(ActionEvent event) {
        Timbre selectedTimbre = tableTimbres.getSelectionModel().getSelectedItem();
        if (selectedTimbre != null) {
            try {
                service.supprimerTimbre(selectedTimbre);
                timbres.remove(selectedTimbre);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer le timbre !");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
