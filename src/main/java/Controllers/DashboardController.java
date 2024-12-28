package Controllers;

import Service.ServicePieceMonnaie;
import Service.ServiceCartePostale;
import Service.ServiceTimbre;
import Service.ServiceLivre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

    @FXML
    private ComboBox<String> comboBoxCollections;
    @FXML
    private VBox vBoxCharts;

    private final ServiceLivre serviceLivre = new ServiceLivre();
    private final ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();
    private final ServiceCartePostale serviceCartePostale = new ServiceCartePostale();
    private final ServiceTimbre serviceTimbre = new ServiceTimbre();

    @FXML
    public void initialize() {
        try {
            // Collections prédéfinies
            ajouterPieChart("Livres", serviceLivre.getNombreTotal(), serviceLivre.getObjectifTotal());
            ajouterPieChart("Timbres", serviceTimbre.getNombreTotal(), serviceTimbre.getObjectifTotal());
            ajouterPieChart("Cartes Postales", serviceCartePostale.getNombreTotal(), serviceCartePostale.getObjectifTotal());
            ajouterPieChart("Pièces de Monnaie", servicePieceMonnaie.getNombreTotal(), servicePieceMonnaie.getObjectifTotal());

            // Collections dynamiques
            ObservableList<String> collectionsDynamiques = getDynamicCollections();
            for (String collection : collectionsDynamiques) {
                int total = getTotalForCollection(collection);
                int objectif = getObjectifForCollection(collection);
                ajouterPieChart(collection, total, objectif);
            }

            // Remplir la ComboBox
            ObservableList<String> collections = FXCollections.observableArrayList(
                    "Livres", "Timbres", "Cartes Postales", "Pièces de Monnaie"
            );
            collections.addAll(collectionsDynamiques);
            comboBoxCollections.setItems(collections);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'initialisation des graphiques : " + e.getMessage());
        }
    }

    private void ajouterPieChart(String nomCollection, int total, int objectif) {
        try {
            PieChart pieChart = new PieChart();
            afficherPieChart(pieChart, total, objectif, nomCollection);

            VBox vBox = new VBox();
            vBox.setStyle("-fx-background-color: #bdc3c7; -fx-padding: 10; -fx-border-color: #2c3e50; -fx-border-width: 1;");
            vBox.setAlignment(javafx.geometry.Pos.CENTER);

            Label label = new Label(nomCollection);
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            vBox.getChildren().addAll(label, pieChart);
            vBoxCharts.getChildren().add(vBox);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ajout du PieChart pour " + nomCollection);
        }
    }

    private void afficherPieChart(PieChart pieChart, int total, int objectif, String nom) {
        try {
            if (objectif <= 0) {
                throw new IllegalArgumentException("L'objectif total doit être supérieur à zéro pour " + nom);
            }

            int restant = Math.max(0, objectif - total);

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                    new PieChart.Data("Atteint", total),
                    new PieChart.Data("Restant", restant)
            );
            pieChart.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'affichage du PieChart pour " + nom + " : " + e.getMessage());
        }
    }

    private ObservableList<String> getDynamicCollections() {
        // Remplacez cette partie par une requête SQL ou un service pour récupérer les collections
        return FXCollections.observableArrayList("Nouvelle Collection 1", "Nouvelle Collection 2");
    }

    private int getTotalForCollection(String collection) {
        // Remplacez par une logique pour récupérer le total de la collection
        return 50; // Exemple
    }

    private int getObjectifForCollection(String collection) {
        // Remplacez par une logique pour récupérer l'objectif de la collection
        return 100; // Exemple
    }

    @FXML
    private void handleCollectionSelection(ActionEvent event) {
        String selectedCollection = comboBoxCollections.getValue();
        if (selectedCollection != null) {
            switch (selectedCollection) {
                case "Livres":
                    navigateToPage("GestionLivres.fxml");
                    break;
                case "Timbres":
                    navigateToPage("GestionTimbre.fxml");
                    break;
                case "Cartes Postales":
                    navigateToPage("GestionCartePostale.fxml");
                    break;
                case "Pièces de Monnaie":
                    navigateToPage("GestionPieceMonnaie.fxml");
                    break;
                default:
                    System.out.println("Navigation pour la collection : " + selectedCollection);
            }
        }
    }

    private void navigateToPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFileName));
            Parent root = loader.load();

            Scene currentScene = comboBoxCollections.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la navigation vers : " + fxmlFileName);
        }
    }
}
