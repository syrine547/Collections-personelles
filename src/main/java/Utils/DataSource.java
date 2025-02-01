package Utils;

import java.sql.*;

public class DataSource {
    private Connection con;
    private static DataSource data;
    private final String url = "jdbc:mysql://localhost:3306/Collections"; // Première base de données
    private final String user = "root";
    private final String pass = "MySQL123!";

    private DataSource() {
        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base Collections : " + e.getMessage());
        }
    }

    //Singleton pour récupérer une instance unique de DataSource.
    public static DataSource getInstance() {
        if (data == null) {
            data = new DataSource();
        }
        return data;
    }

    //Retourne une connexion active à la base de données.
    public Connection getCon() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(url, user, pass);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
        }
        return con;
    }

    public static String checkLogin(String username, String password) {
        String query = "SELECT id, role FROM Collections.users WHERE (username = ? OR email = ?) AND password = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String role = rs.getString("role");
                    return userId + ":" + role; // Retourne l'ID et le rôle sous forme de String
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Échec de connexion
    }

    public static boolean checkEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM Collections.users WHERE email = ?";
        try (Connection conn = DataSource.getInstance().getCon(); // Accès à la connexion via l'instance Singleton
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retourne true si l'email existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}