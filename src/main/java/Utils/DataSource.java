package Utils;

import java.sql.*;

public class DataSource {
    private Connection con;
    private static DataSource data;
    private final String url = "jdbc:mysql://localhost:3306/Collections"; // Première base de données
    private final String user = "root";
    private final String pass = "root";

    private DataSource() {
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion établie à la base de données Collections.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base Collections : " + e.getMessage());
        }
    }

    /**
     * Singleton pour récupérer une instance unique de DataSource.
     */
    public static DataSource getInstance() {
        if (data == null) {
            data = new DataSource();
        }
        return data;
    }

    /**
     * Retourne une connexion active à la base de données.
     */
    public Connection getCon() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Nouvelle connexion établie.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
        }
        return con;
    }

    /**
     * Vérifie les identifiants de connexion d'un utilisateur.
     *
     * @param username Nom d'utilisateur ou email.
     * @param password Mot de passe.
     * @return true si les identifiants sont valides.
     */
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


    /**
     * Vérifie si un email existe déjà dans la base de données.
     *
     * @param email Email à vérifier.
     * @return true si l'email existe.
     */
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

    /**
     * Met à jour l'email d'un utilisateur.
     *
     * @param username Nom d'utilisateur.
     * @param newEmail Nouvel email.
     * @return true si la mise à jour a réussi.
     */
    public boolean updateEmail(String username, String newEmail) {
        String query = "UPDATE Collections.users SET email = ? WHERE username = ?";
        try (Connection conn = getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newEmail);
            stmt.setString(2, username);

            return stmt.executeUpdate() > 0; // Retourne true si une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Ferme la connexion à la base de données.
     */
    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}