package Controllers;

import Service.*;
import Utils.DataSource;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.PreparedStatement;

public class DashboardController {

    @FXML
    private ComboBox<String> comboBoxCollections;
    @FXML
    private VBox vBoxCharts;

    @FXML
    public void initialize() {
        try {
            // Récupération des collections dynamiques depuis la base de données
            ObservableList<String> collectionsDynamiques = getDynamicCollections();

            // Ajout des PieCharts pour chaque collection dynamique
            for (String collection : collectionsDynamiques) {
                int total = getTotalForCollection(collection);
                int objectif = getObjectifForCollection(collection);
                ajouterPieChart(collection, total, objectif);
            }

            // Remplir la ComboBox
            comboBoxCollections.setItems(collectionsDynamiques);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'initialisation des graphiques : " + e.getMessage());
        }
    }

    private ObservableList<String> getDynamicCollections() {
        ObservableList<String> collections = FXCollections.observableArrayList();
        String query = "SELECT nomType FROM Collections.typesExistants";

        try (Connection con = DataSource.getInstance().getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                collections.add(rs.getString("nomType"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des collections dynamiques : " + e.getMessage());
            // You can display an error message to the user here
        }

        return collections;
    }
    @FXML
    private void handleCollectionSelection(ActionEvent event) {
        String selectedCollection = comboBoxCollections.getValue(); // Récupérer la sélection
        if (selectedCollection != null) {
            // Rechercher l'action ou la page associée à la collection dans la base de données
            String fxmlFile = getFxmlFileForCollection(selectedCollection);

            if (fxmlFile != null && !fxmlFile.isEmpty()) {
                navigateToPage(fxmlFile);
            } else {
                System.out.println("Aucune page FXML associée à la collection : " + selectedCollection);
            }
        } else {
            System.out.println("Aucune collection sélectionnée.");
        }
    }

    private String getFxmlFileForCollection(String collectionName) {
        String fxmlFile = null;
        String query = "SELECT fxmlFile FROM Collections.typesExistants WHERE nomType = ?";

        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, collectionName); // Associer le nom de la collection
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fxmlFile = rs.getString("fxmlFile"); // Récupérer le nom du fichier FXML
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération du fichier FXML pour la collection : " + collectionName);
        }

        return fxmlFile;
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

    private int getTotalForCollection(String collection) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections.`" + collection + "`"; // Utilisation de COUNT(*)
        try (Connection con = DataSource.getInstance().getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0; // Retourne 0 s'il y a une erreur ou si la table est vide
    }

    private int getObjectifForCollection(String collection) throws SQLException {
        String query = "SELECT objectif_total FROM Collections.typesExistants WHERE nomType = ?";
        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, collection);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("objectif_total");
                }
            }
        }
        return 0; // Handle cases where there's no objective or collection not found
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

    @FXML
    private void handleAjouterCollection(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutCollection.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une collection");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDynamicCollection(String collectionName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCollection.fxml"));
            Parent root = loader.load();

            GestionCollectionController controller = loader.getController();
            controller.setNomCollection(collectionName);

            Stage stage = (Stage) comboBoxCollections.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la navigation vers gestionCollection.fxml");
        }
    }

    private List<String> getAttributsForCollection(String collectionName) {
        // Retourne les attributs de la collection (à personnaliser selon vos besoins)
        if ("Nouvelle Collection 1".equals(collectionName)) {
            return List.of("Nom", "Catégorie", "Valeur");
        } else if ("Nouvelle Collection 2".equals(collectionName)) {
            return List.of("Titre", "Auteur", "Année");
        } else {
            return List.of("Attribut 1", "Attribut 2"); // Exemple par défaut
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
}
