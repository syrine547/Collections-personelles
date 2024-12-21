package Controllers;

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

public class GestionLivresController {

    // Champs du formulaire
    @FXML private TextField fieldTitre;
    @FXML private TextField fieldAuteur;
    @FXML private TextField fieldAnnee;
    @FXML private TextField fieldQuantite;

    // TableView et colonnes
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Integer> colQuantite;

    // Liste observable pour les livres
    private ObservableList<Livre> livres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colTitre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitre()));
        colAuteur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuteur()));
        colAnnee.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAnnee()).asObject());
        colQuantite.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());

        // Charger les livres existants dans la TableView
        tableLivres.setItems(livres);
    }

    @FXML
    private void handleAjouterLivre(ActionEvent event) {
        try {
            String titre = fieldTitre.getText();
            String auteur = fieldAuteur.getText();
            int annee = Integer.parseInt(fieldAnnee.getText());
            int quantite = Integer.parseInt(fieldQuantite.getText());

            Livre livre = new Livre(titre, auteur, annee, quantite);
            livres.add(livre);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour l'année et la quantité.");
        }
    }

    @FXML
    private void handleModifierLivre(ActionEvent event) {
        Livre selectedLivre = tableLivres.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            try {
                selectedLivre.setTitre(fieldTitre.getText());
                selectedLivre.setAuteur(fieldAuteur.getText());
                selectedLivre.setAnnee(Integer.parseInt(fieldAnnee.getText()));
                selectedLivre.setQuantite(Integer.parseInt(fieldQuantite.getText()));
                tableLivres.refresh();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Veuillez entrer des valeurs valides pour l'année et la quantité.");
            }
        } else {
            showAlert("Aucun livre sélectionné", "Veuillez sélectionner un livre à modifier.");
        }
    }

    @FXML
    private void handleSupprimerLivre(ActionEvent event) {
        Livre selectedLivre = tableLivres.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            livres.remove(selectedLivre);
            clearFields();
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
        fieldAnnee.clear();
        fieldQuantite.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe Livre pour représenter les données
    public static class Livre {
        private final SimpleStringProperty titre;
        private final SimpleStringProperty auteur;
        private final SimpleIntegerProperty annee;
        private final SimpleIntegerProperty quantite;

        public Livre(String titre, String auteur, int annee, int quantite) {
            this.titre = new SimpleStringProperty(titre);
            this.auteur = new SimpleStringProperty(auteur);
            this.annee = new SimpleIntegerProperty(annee);
            this.quantite = new SimpleIntegerProperty(quantite);
        }

        public String getTitre() { return titre.get(); }
        public void setTitre(String titre) { this.titre.set(titre); }

        public String getAuteur() { return auteur.get(); }
        public void setAuteur(String auteur) { this.auteur.set(auteur); }

        public int getAnnee() { return annee.get(); }
        public void setAnnee(int annee) { this.annee.set(annee); }

        public int getQuantite() { return quantite.get(); }
        public void setQuantite(int quantite) { this.quantite.set(quantite); }
    }
}
