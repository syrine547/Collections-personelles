package Service;
import Utils.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void createLogsTable() {
        String sqlLogs = "CREATE TABLE IF NOT EXISTS logss (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "action VARCHAR(255) NOT NULL," +
                "collection_id INT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ");";

        try (Connection conn = DataSource.getInstance().getCon();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlLogs);
            System.out.println("Table de journalisation créée avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

