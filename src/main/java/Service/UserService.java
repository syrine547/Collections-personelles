package Service;

import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {

    public void addUser(String username, String password) {
        String sql = "INSERT INTO Collections.users (username, password) VALUES (?, ?)";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Utilisateur ajouté avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }
}