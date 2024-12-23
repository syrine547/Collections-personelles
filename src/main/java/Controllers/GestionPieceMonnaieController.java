package Controllers;

import Entity.Livre;
import Entity.PieceMonnaie;
import Service.ServicePieceMonnaie;
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

public class GestionPieceMonnaieController {

    @FXML private TextField fieldValeur;
    @FXML private TextField fieldUnite;
    @FXML private TextField fieldQuantite;

    @FXML private TableView<PieceMonnaie> tablePieces;
    @FXML private TableColumn<PieceMonnaie, String> colValeur;
    @FXML private TableColumn<PieceMonnaie, String> colUnite;
    @FXML private TableColumn<PieceMonnaie, Integer> colQuantite;

    private ObservableList<PieceMonnaie> pieces = FXCollections.observableArrayList();
    private ServicePieceMonnaie service = new ServicePieceMonnaie();

    private void chargerPieceMonnaieDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie, quantité FROM PiecesMonnaie";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("PiecesMonnaie");
                String valeur = resultSet.getString("valeurPiecesMonnaie");
                String unité = resultSet.getString("unitéPiecesMonnaie");
                int quantite = resultSet.getInt("quantité");

                // Crée un objet Livre et l'ajoute à la liste observable
                PieceMonnaie pieceMonnaie = new PieceMonnaie(id, valeur, unité);
                pieceMonnaie.setQuantite(quantite); // Définir la quantité si elle n'est pas dans le constructeur
                pieces.add(pieceMonnaie);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche le stacktrace pour le débogage
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        colValeur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValeurPiecesMonnaie()));
        colUnite.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnitéPiecesMonnaie()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        chargerPieceMonnaieDepuisBD();
        tablePieces.setItems(pieces);
    }

    @FXML
    private void handleAjouterPiece(ActionEvent event) {
        try {
            String valeur = fieldValeur.getText();
            String unite = fieldUnite.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            PieceMonnaie piece = new PieceMonnaie(0, valeur, unite, quantite);
            service.ajouterPieceMonnaie(piece);
            pieces.add(piece);
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter la pièce !");
        }
    }

    @FXML
    private void handleModifierPiece(ActionEvent event) {
        PieceMonnaie selectedPiece = tablePieces.getSelectionModel().getSelectedItem();
        if (selectedPiece != null) {
            try {
                selectedPiece.setValeurPiecesMonnaie(fieldValeur.getText());
                selectedPiece.setUnitéPiecesMonnaie(fieldUnite.getText());
                selectedPiece.setQuantite(Integer.parseInt(fieldQuantite.getText()));
                service.updatePieceMonnaie(selectedPiece);
                tablePieces.refresh();
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier la pièce !");
            }
        }
    }

    @FXML
    private void handleSupprimerPiece(ActionEvent event) {
        PieceMonnaie selectedPiece = tablePieces.getSelectionModel().getSelectedItem();
        if (selectedPiece != null) {
            try {
                service.supprimerPieceMonnaie(selectedPiece);
                pieces.remove(selectedPiece);
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer la pièce !");
            }
        }
    }

    private void clearFields() {
        fieldValeur.clear();
        fieldUnite.clear();
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
