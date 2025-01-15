package Controllers;

import Service.ServiceCollection;
import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.sql.PreparedStatement;
import java.util.Map;

public class DashboardController {

    @FXML
    private ComboBox<String> comboBoxCollections;
    @FXML
    private VBox vBoxCharts;
    @FXML
    private GridPane gridCharts;


    @FXML
    public void initialize() {
        try {
            System.out.println("Chargement des collections dynamiques...");
            ObservableList<String> collectionsDynamiques = getDynamicCollections();
            System.out.println("Collections trouvées : " + collectionsDynamiques);

            comboBoxCollections.setItems(collectionsDynamiques);

            for (String collection : collectionsDynamiques) {
                System.out.println("Ajout du PieChart pour : " + collection);
                int total = getTotalForCollection(collection);
                int objectif = getObjectifForCollection(collection);
                ajouterPieChart(collection, total, objectif);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation des graphiques : " + e.getMessage());
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
            showAlert("Erreur", "Erreur lors de la récupération des collections dynamiques : " + e.getMessage());
        }

        return collections;
    }

    private void navigateToDynamicCollection(String collectionName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCollection.fxml"));
            Parent root = loader.load();

            GenericCollectionController controller = loader.getController();
            controller.setNomCollection(collectionName);

            Stage stage = (Stage) comboBoxCollections.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la collection : " + collectionName);
        }
    }

    private void ajouterPieChart(String nomCollection, int total, int objectif) {
        try {
            // Créer le PieChart
            PieChart pieChart = new PieChart();
            afficherPieChart(pieChart, total, objectif, nomCollection);

            // Créer une VBox pour le graphique et les boutons
            VBox vBox = new VBox(5); // Espacement entre les éléments
            vBox.setStyle("-fx-background-color: #bdc3c7; -fx-padding: 10; -fx-border-color: #2c3e50; -fx-border-width: 1;");
            vBox.setAlignment(javafx.geometry.Pos.CENTER);

            // Ajouter un label pour le nom de la collection
            Label label = new Label(nomCollection);
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Ajouter les boutons Modifier et Supprimer
            Button btnModifier = new Button("Modifier");
            btnModifier.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
            btnModifier.setOnAction(event -> handleModifierCollection(nomCollection));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            btnSupprimer.setOnAction(event -> handleSupprimerCollection(nomCollection));

            // Ajouter le PieChart, le label et les boutons dans la VBox
            HBox buttonsBox = new HBox(10, btnModifier, btnSupprimer);
            buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);

            vBox.getChildren().addAll(label, pieChart, buttonsBox);

            // Calcul de la position dans la grille
            int index = gridCharts.getChildren().size();
            int row = index / 3; // Supposez 3 colonnes
            int col = index % 3;

            // Ajouter la VBox dans le GridPane
            gridCharts.add(vBox, col, row);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du PieChart pour " + nomCollection);
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
            showAlert("Erreur", "Erreur lors de l'affichage du PieChart pour " + nom);
        }
    }

    @FXML
    private void handleCollectionSelection(ActionEvent event) {
        String selectedCollection = comboBoxCollections.getValue(); // Récupérer la sélection
        if (selectedCollection != null) {
            // Rechercher l'action ou la page associée à la collection dans la base de données
            navigateToDynamicCollection(selectedCollection);
        } else {
            System.out.println("Aucune collection sélectionnée.");
        }
    }

    private int getTotalForCollection(String collection) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections." + collection + ""; // Utilisation de COUNT(*)
        try (Connection con = DataSource.getInstance().getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
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
        return 0;
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

    private void handleSupprimerCollection(String nomCollection) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Êtes-vous sûr de vouloir supprimer la collection " + nomCollection + " ? Cette action est irréversible.",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (ServiceCollection service = new ServiceCollection(nomCollection)) {
                    if (service.supprimerCollection(nomCollection)) {
                        showAlert("Succès", "Collection supprimée avec succès !");
                        refreshCharts();
                    } else {
                        showAlert("Erreur", "Échec de la suppression de la collection.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleModifierCollection(String nomCollection) {
        try (ServiceCollection service = new ServiceCollection(nomCollection)) {
            int objectifTotal = service.getObjectifTotalParNom(nomCollection);
            ObservableList<Map<String, String>> attributs = service.getAttributsAvecTypes();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierCollection.fxml"));
            Parent root = loader.load();

            ModifierCollectionController controller = loader.getController();
            controller.setCollectionDetails(nomCollection, objectifTotal, attributs);

            Stage stage = new Stage();
            stage.setTitle("Modifier la Collection");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir les données après modification
            refreshCharts();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification de la collection : " + e.getMessage());
        }
    }

    private void refreshCharts() {
        // Effacer les anciens graphiques du GridPane
        gridCharts.getChildren().clear();

        try {
            // Récupérer toutes les collections dynamiques
            ObservableList<String> collectionsDynamiques = getDynamicCollections();

            int colCount = 3; // Nombre de colonnes dans la grille
            int index = 0;

            // Parcourir les collections pour ajouter les graphiques
            for (String collection : collectionsDynamiques) {
                int row = index / colCount; // Calcul de la ligne
                int col = index % colCount; // Calcul de la colonne

                // Récupérer les données pour la collection
                int total = getTotalForCollection(collection);
                int objectif = getObjectifForCollection(collection);

                // Ajouter le PieChart dans la grille
                ajouterPieChartDansGrille(collection, total, objectif, row, col);

                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du rafraîchissement des graphiques : " + e.getMessage());
        }
    }

    private void ajouterPieChartDansGrille(String nomCollection, int total, int objectif, int row, int col) {
        // Créer un PieChart
        PieChart pieChart = new PieChart();
        afficherPieChart(pieChart, total, objectif, nomCollection);

        // Créer une VBox pour contenir le PieChart et le Label
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #bdc3c7; -fx-padding: 10; -fx-border-color: #2c3e50; -fx-border-width: 1;");

        // Ajouter un Label pour le nom de la collection
        Label label = new Label(nomCollection);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        vBox.getChildren().addAll(label, pieChart);

        // Ajouter la VBox dans le GridPane
        gridCharts.add(vBox, col, row);
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
