package Service;
import Utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogService {

    public static void enregistrerAction(int userId, String action, Integer collectionId) {
        String sql = "INSERT INTO logss (user_id, action, collection_id) VALUES (?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            if (collectionId != null) {
                pstmt.setInt(3, collectionId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            pstmt.executeUpdate();
            System.out.println("Action enregistrée: " + action);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

