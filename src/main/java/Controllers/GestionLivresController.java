package Controllers;

import Service.ServiceLivre;
import Entity.Livre;
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

import java.sql.*;

public class GestionLivresController {

    // Champs du formulaire
    @FXML private TextField fieldTitre;
    @FXML private TextField fieldAuteur;
    @FXML private TextField fieldQuantite;

    // TableView et colonnes
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, Integer> colQuantite;

    // Liste observable pour les livres
    private ObservableList<Livre> livres = FXCollections.observableArrayList();

    private void chargerLivresDepuisBD() {
        try (Connection connection = DataSource.getInstance().getCon()) {
            String query = "SELECT idLIvre, titreLivre, auteurLivre, quantité FROM Livres";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("idLIvre");
                String titre = resultSet.getString("titreLivre");
                String auteur = resultSet.getString("auteurLivre");
                int quantite = resultSet.getInt("quantité");

                // Crée un objet Livre et l'ajoute à la liste observable
                Livre livre = new Livre(id, titre, auteur);
                livre.setQuantite(quantite); // Définir la quantité si elle n'est pas dans le constructeur
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche le stacktrace pour le débogage
            showAlert("Erreur de connexion", "Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colTitre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitreLivre()));
        colAuteur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuteurLivre()));
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Charger les livres existants depuis la base de données
        chargerLivresDepuisBD();

        // Associer les données à la TableView
        tableLivres.setItems(livres);
    }

    @FXML
    private void handleAjouterLivre(ActionEvent event) {
        try {
            String titre = fieldTitre.getText();
            String auteur = fieldAuteur.getText();
            int quantite = Integer.parseInt(fieldQuantite.getText());

            Livre livre = new Livre(0, titre, auteur); // Ajustez selon votre constructeur
            livre.setQuantite(quantite); // Ajoutez la quantité

            // Appeler le service pour ajouter le livre dans la base de données
            ServiceLivre serviceLivre = new ServiceLivre();
            boolean isAdded = serviceLivre.ajouterLivre(livre);  // Méthode qui ajoute le livre à la base de données

            if (isAdded) {
                // Si l'ajout a réussi, ajouter le livre à la liste observable et mettre à jour la TableView
                livres.add(livre);
                clearFields();// Réinitialiser les champs du formulaire
                showAlert("Succès", "Le livre a été ajouté avec succès.");
            } else {
                showAlert("Erreur", "Le livre n'a pas pu être ajouté à la base de données.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Erreur lors de l'ajout du livre à la base de données. "+ e.getMessage());
        }
    }

    @FXML
    private void handleModifierLivre(ActionEvent event) {
        Livre selectedLivre = tableLivres.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            try {
                // Mettre à jour les valeurs dans l'objet sélectionné
                selectedLivre.setTitreLivre(fieldTitre.getText());
                selectedLivre.setAuteurLivre(fieldAuteur.getText());
                selectedLivre.setQuantite(Integer.parseInt(fieldQuantite.getText()));

                // Appeler le service pour mettre à jour dans la base de données
                ServiceLivre serviceLivre = new ServiceLivre();
                boolean isUpdated = serviceLivre.updateLivre(selectedLivre);

                if (isUpdated) {
                    tableLivres.refresh(); // Rafraîchir la TableView
                    clearFields(); // Réinitialiser les champs
                    showAlert("Succès", "Le livre a été modifié avec succès.");
                } else {
                    showAlert("Erreur", "Le livre n'a pas pu être modifié dans la base de données.");
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour la quantité.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la mise à jour du livre : " + e.getMessage());
            }
        } else {
            showAlert("Aucun livre sélectionné", "Veuillez sélectionner un livre à modifier.");
        }
    }


    @FXML
    private void handleSupprimerLivre(ActionEvent event) {
        Livre selectedLivre = tableLivres.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            try {
                // Appeler le service pour supprimer dans la base de données
                ServiceLivre serviceLivre = new ServiceLivre();
                boolean isDeleted = serviceLivre.supprimerLivre(selectedLivre);

                if (isDeleted) {
                    livres.remove(selectedLivre); // Supprimer de la liste observable
                    clearFields(); // Réinitialiser les champs
                    showAlert("Succès", "Le livre a été supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Le livre n'a pas pu être supprimé de la base de données.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la suppression du livre : " + e.getMessage());
            }
        } else {
            showAlert("Aucun livre sélectionné", "Veuillez sélectionner un livre à supprimer.");
        }
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Stage stage = (Stage) tableLivres.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard.");
        }
    }

    private void clearFields() {
        fieldTitre.clear();
        fieldAuteur.clear();
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
