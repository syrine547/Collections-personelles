package Controllers;

import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogsController {

    @FXML
    private TableView<LogEntry> tableLogs;

    @FXML
    private TableColumn<LogEntry, Integer> colId;

    @FXML
    private TableColumn<LogEntry, Integer> colUserId;

    @FXML
    private TableColumn<LogEntry, String> colAction;

    @FXML
    private TableColumn<LogEntry, Integer> colCollectionId;

    @FXML
    private TableColumn<LogEntry, String> colTimestamp;

    private ObservableList<LogEntry> logss = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes de la table
        System.out.println("✅ LogsController chargé !");
        //colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        //colCollectionId.setCellValueFactory(new PropertyValueFactory<>("collectionId"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Charger les journaux
        loadLogs();
    }
    /*@FXML
    private void handleRetourDashboard() {
        try {
            Stage currentStage = (Stage) tableLogs.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au Dashboard : " + e.getMessage());
        }
    }*/


    @FXML
    private void handleRetourDashboard() {
        try {
            // Récupérer l'ID utilisateur actuellement connecté
            int userId = AuthController.currentUserId;

            // Charger le FXML du Dashboard
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

    private void loadLogs() {
        logss.clear();
        String query = "SELECT id, user_id, action, collection_id, timestamp " +
                "FROM Collections.logss ORDER BY timestamp DESC";
        try (Connection con = DataSource.getInstance().getCon()) {
            if (con == null || con.isClosed()) {
                System.err.println("❌ ERREUR : Impossible d'obtenir une connexion à la base de données !");
                return;         }
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                int count = 0;
                while (rs.next()) {
                    LogEntry log = new LogEntry(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action"),
                            rs.getInt("collection_id"),
                            rs.getString("timestamp")
                    );
                    logss.add(log);
                    count++;
                }

            tableLogs.setItems(logss);
            System.out.println("✅ Nombre de logs récupérés : " + count);
        }} catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des journaux : " + e.getMessage());
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
        private final int userId;
        private final String action;
        private final int collectionId;
        private final String timestamp;

        public LogEntry(int id, int userId, String action, int collectionId, String timestamp) {
            this.id = id;
            this.userId = userId;
            this.action = action;
            this.collectionId = collectionId;
            this.timestamp = timestamp;
        }

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getAction() {
            return action;
        }

        public int getCollectionId() {
            return collectionId;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
