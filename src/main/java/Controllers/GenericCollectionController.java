package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import Service.ServiceCollection;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GenericCollectionController {
    @FXML
    private Label labelTitre;

    @FXML
    private TableView<Map<String, Object>> tableElements;

    private String nomCollection;

    /**
     * Configurer la collection dynamique.
     */
    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
        labelTitre.setText("Gestion de la collection : " + nomCollection);
        chargerDonnees();
    }

    /**
     * Charger dynamiquement les données et les colonnes.
     */
    private void chargerDonnees() {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            // Charger les attributs (colonnes)
            List<String> attributs = service.getAttributs();

            // Ajouter dynamiquement les colonnes au TableView
            for (String attribut : attributs) {
                TableColumn<Map<String, Object>, Object> colonne = new TableColumn<>(attribut);
                colonne.setCellValueFactory(new PropertyValueFactory<>(attribut));
                tableElements.getColumns().add(colonne);
            }

            // Charger les données
            List<Map<String, Object>> donnees = service.readAll();
            tableElements.getItems().addAll(donnees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données pour la collection : " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterElement() {
        // Code pour ajouter un nouvel élément (peut ouvrir une boîte de dialogue pour saisir les valeurs dynamiques)
        System.out.println("Ajout d'un nouvel élément dans la collection : " + nomCollection);
    }

    @FXML
    private void handleRetourDashboard() {
        // Code pour revenir au tableau de bord principal
        System.out.println("Retour au Dashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
