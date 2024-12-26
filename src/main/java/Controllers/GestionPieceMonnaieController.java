package Controllers;

import Entity.PieceMonnaie;
import Service.ServicePieceMonnaie;
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

public class GestionPieceMonnaieController {

    // Champs du formulaire
    @FXML private TextField fieldValeur;
    @FXML private TextField fieldUnite;
    @FXML private TextField fieldQuantite;

    // TableView et colonnes
    @FXML private TableView<PieceMonnaie> tablePieces;
    @FXML private TableColumn<PieceMonnaie, String> colValeur;
    @FXML private TableColumn<PieceMonnaie, String> colUnite;
    @FXML private TableColumn<PieceMonnaie, Integer> colQuantite;

    // Liste observable pour les pièces
    private ObservableList<PieceMonnaie> pieces = FXCollections.observableArrayList();

    private void chargerPiecesDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie, quantité FROM PiecesMonnaie";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idPiecesMonnaie");
                String valeur = resultSet.getString("valeurPiecesMonnaie");
                String unite = resultSet.getString("unitéPiecesMonnaie");
                int quantite = resultSet.getInt("quantité");

                PieceMonnaie piece = new PieceMonnaie(id, valeur, unite);
                piece.setQuantite(quantite);
                pieces.add(piece);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colValeur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValeurPiecesMonnaie()));
        colUnite.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnitéPiecesMonnaie()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Charger les pièces depuis la base de données
        chargerPiecesDepuisBD();

        // Associer les données à la TableView
        tablePieces.setItems(pieces);
    }

    @FXML
    private void handleAjouterPiece(ActionEvent event) {
        try {
            String valeur = fieldValeur.getText();
            String unite = fieldUnite.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            PieceMonnaie piece = new PieceMonnaie(0, valeur, unite);
            piece.setQuantite(quantite);

            // Ajouter à la base de données via le service
            ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();
            boolean isAdded = servicePieceMonnaie.ajouterPieceMonnaie(piece);

            if (isAdded) {
                pieces.add(piece);
                clearFields();
                showAlert("Succès", "La pièce a été ajoutée avec succès.");
            } else {
                showAlert("Erreur", "La pièce n'a pas pu être ajoutée à la base de données.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Erreur lors de l'ajout de la pièce : " + e.getMessage());
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

                ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();
                boolean isUpdated = servicePieceMonnaie.updatePieceMonnaie(selectedPiece);

                if (isUpdated) {
                    tablePieces.refresh();
                    clearFields();
                    showAlert("Succès", "La pièce a été modifiée avec succès.");
                } else {
                    showAlert("Erreur", "La pièce n'a pas pu être modifiée.");
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la modification de la pièce : " + e.getMessage());
            }
        } else {
            showAlert("Aucune pièce sélectionnée", "Veuillez sélectionner une pièce à modifier.");
        }
    }

    @FXML
    private void handleSupprimerPiece(ActionEvent event) {
        PieceMonnaie selectedPiece = tablePieces.getSelectionModel().getSelectedItem();
        if (selectedPiece != null) {
            try {
                ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();
                boolean isDeleted = servicePieceMonnaie.supprimerPieceMonnaie(selectedPiece);

                if (isDeleted) {
                    pieces.remove(selectedPiece);
                    clearFields();
                    showAlert("Succès", "La pièce a été supprimée avec succès.");
                } else {
                    showAlert("Erreur", "La pièce n'a pas pu être supprimée.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la suppression de la pièce : " + e.getMessage());
            }
        } else {
            showAlert("Aucune pièce sélectionnée", "Veuillez sélectionner une pièce à supprimer.");
        }
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Stage stage = (Stage) tablePieces.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard.");
        }
    }

    private void clearFields() {
        fieldValeur.clear();
        fieldUnite.clear();
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
