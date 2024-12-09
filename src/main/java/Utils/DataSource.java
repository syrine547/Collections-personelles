package Utils;
import java.sql.*;

public class DataSource {
    private Connection con;
    private static DataSource data;
    private String url = "jdbc:mysql://localhost:3306/Collections";
    private String user = "root";
    private String pass = "MySQL123!";

    private DataSource() {
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("connexion etablie");
        }catch (SQLException e){
            System.out.println(e);
        }
    }

    public Connection getCon() {
        return con;
    }

    public static DataSource getInstance() {
        if (data == null) {
            data = new DataSource();

        }
        return data;
    }

}
