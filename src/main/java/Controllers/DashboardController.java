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

public class DashboardController {

    @FXML private ComboBox<String> comboBoxCollections;

    @FXML private PieChart pieChartLivres;
    @FXML private PieChart pieChartPieces;
    @FXML private PieChart pieChartCartes;
    @FXML private PieChart pieChartTimbres;

    private final ServiceLivre serviceLivre = new ServiceLivre();
    private final ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();
    private final ServiceCartePostale serviceCartePostale = new ServiceCartePostale();
    private final ServiceTimbre serviceTimbre = new ServiceTimbre();

    @FXML
    public void initialize() {
        try {
            afficherPieChart(pieChartLivres, serviceLivre.getNombreTotal(), serviceLivre.getObjectifTotal(), "Livres");
            afficherPieChart(pieChartPieces, servicePieceMonnaie.getNombreTotal(), servicePieceMonnaie.getObjectifTotal(), "Pièces de Monnaie");
            afficherPieChart(pieChartCartes, serviceCartePostale.getNombreTotal(), serviceCartePostale.getObjectifTotal(), "Cartes Postales");
            afficherPieChart(pieChartTimbres, serviceTimbre.getNombreTotal(), serviceTimbre.getObjectifTotal(), "Timbres");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'initialisation des graphiques : " + e.getMessage());
        }
        // Initialiser la liste des collections dans la ComboBox
        ObservableList<String> collections = FXCollections.observableArrayList(
                "Livres", "Timbres", "Cartes Postales", "Pièces de Monnaie"
        );
        comboBoxCollections.setItems(collections);
    }

    @FXML
    private void handleCollectionSelection(ActionEvent event) {
        String selectedCollection = comboBoxCollections.getValue(); // Récupérer la sélection
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
                    System.out.println("Collection inconnue : " + selectedCollection);
            }
        }
    }

    @FXML
    private void handleNavigateToProfil(ActionEvent event) {
        System.out.println("Naviguer vers le Profil");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Déconnexion...");
    }

    /**
     * Affiche un graphique circulaire pour une collection donnée.
     * @param pieChart Le graphique à mettre à jour.
     * @param total Nombre actuel d'éléments collectés.
     * @param objectif Objectif total à atteindre.
     * @param nom Nom de la collection (pour le débogage).
     */
    private void afficherPieChart(PieChart pieChart, int total, int objectif, String nom) {
        try {
            if (objectif <= 0) {
                throw new IllegalArgumentException("L'objectif total doit être supérieur à zéro pour " + nom);
            }

            int restant = Math.max(0, objectif - total); // Empêche les valeurs négatives

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

    private void navigateToPage(String fxmlFileName) {
        try {
            // Charger la nouvelle scène
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/" + fxmlFileName));
            javafx.scene.Parent root = loader.load();

            // Changer la scène actuelle
            javafx.scene.Scene currentScene = comboBoxCollections.getScene();
            javafx.stage.Stage stage = (javafx.stage.Stage) currentScene.getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la navigation vers : " + fxmlFileName);
        }
    }
}
