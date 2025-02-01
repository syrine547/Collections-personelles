package Controllers;

import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class LogsController {

    @FXML
    private TableView<LogEntry> tableLogs;

    @FXML
    private TableColumn<LogEntry, String> colCollectionName;

    @FXML
    private TableColumn<LogEntry, String> colAction;

    @FXML
    private TableColumn<LogEntry, String> colTimestamp;

    private ObservableList<LogEntry> logss = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes de la table
        colCollectionName.setCellValueFactory(new PropertyValueFactory<>("collectionName"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        loadLogs(getCurrentUserId()); // Charger les journaux pour l'utilisateur actuel
    }

    private Integer getCurrentUserId() {
        return AuthController.currentUserId; // Exemple : utilisateur connecté
    }

    private void loadLogs(int userId) {
        logss.clear();
        String query = "SELECT l.id, l.user_id, l.action, l.collection_id, l.timestamp, " +
                "t.nomType AS collection_name " + // Récupérer le nom de la collection
                "FROM Collections.logss l " +
                "LEFT JOIN Collections.typesExistants t ON l.collection_id = t.id " + // Jointure pour obtenir le nom de la collection
                "WHERE l.user_id = ? " +
                "ORDER BY l.timestamp DESC";

        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement stmt = con.prepareStatement(query)) {

            if (con == null || con.isClosed()) {
                System.err.println("❌ ERREUR : Impossible d'obtenir une connexion à la base de données !");
                return;
            }

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LogEntry log = new LogEntry(
                            rs.getInt("id"),
                            rs.getString("collection_name"), // Stocke le nom de la collection
                            rs.getString("action"),
                            rs.getString("timestamp")
                    );
                    logss.add(log);
                }
                tableLogs.setItems(logss);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des journaux : " + e.getMessage());
        }
    }

    @FXML
    private void handleRetourDashboard() {
        try {
            int userId = AuthController.currentUserId;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du Dashboard et passer l'ID utilisateur
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserId(userId);  // Passe l'ID utilisateur au contrôleur du Dashboard
            }

            // Mettre à jour la scène avec le Dashboard
            Stage currentStage = (Stage) tableLogs.getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Classe interne pour représenter une entrée de journal
    public static class LogEntry {
        private final int id;
        private final String collectionName;
        private final String action;
        private final String timestamp;

        public LogEntry(int id, String collectionName, String action, String timestamp) {
            this.id = id;
            this.collectionName = collectionName;
            this.action = action;
            this.timestamp = timestamp;
        }

        public int getId() {
            return id;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public String getAction() {
            return action;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}