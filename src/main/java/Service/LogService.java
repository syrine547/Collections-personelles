package Service;

import Utils.DataSource;
import java.sql.*;

public class LogService {

    /**
     * Enregistre une action dans le journal des logs.
     *
     * @param userId      ID de l'utilisateur ayant effectué l'action
     * @param action      Description de l'action effectuée
     * @param collectionId ID de la collection concernée (peut être null)
     */
    public static void enregistrerAction(int userId, String action, Integer collectionId) {
        String sql = "INSERT INTO Collections.logss (user_id, action, collection_id) VALUES (?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            if (collectionId != null) {
                pstmt.setInt(3, collectionId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Log ajouté : " + action + " pour user " + userId);
            } else {
                System.err.println("❌ ERREUR : Impossible d'ajouter le log.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ ERREUR SQL lors de l'ajout d'un log : " + e.getMessage());
        }
    }
}
