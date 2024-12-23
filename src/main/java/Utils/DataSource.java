package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private Connection con;
    private static DataSource data;
    private String url = "jdbc:mysql://localhost:3306/Collections";
    private String user = "root";
    private String pass = "MySQL123!";

    private DataSource() {
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public Connection getCon() {
        try {
            // Vérifiez si la connexion est fermée et recréez-la si nécessaire
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Nouvelle connexion établie");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
        }
        return con;
    }

    public static DataSource getInstance() {
        if (data == null) {
            data = new DataSource();
        }
        return data;
    }
}
