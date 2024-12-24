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

public class DashboardController {

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
    }

    @FXML
    private void handleNavigateToLivres(ActionEvent event) {
        System.out.println("Naviguer vers Livres");
    }

    @FXML
    private void handleNavigateToTimbres(ActionEvent event) {
        System.out.println("Naviguer vers Timbres");
    }

    @FXML
    private void handleNavigateToCartes(ActionEvent event) {
        System.out.println("Naviguer vers Cartes Postales");
    }

    @FXML
    private void handleNavigateToPieces(ActionEvent event) {
        System.out.println("Naviguer vers Pièces de Monnaie");
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
}
