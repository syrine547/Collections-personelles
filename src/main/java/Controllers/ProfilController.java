package Controllers;

import Utils.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.*;

public class ProfilController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label dateNaissanceLabel;
    @FXML
    private Label adresseLabel;
    @FXML
    private Label telephoneLabel;
    @FXML
    private Label professionLabel;
    @FXML
    private Label nationaliteLabel;
    @FXML
    private Label languesLabel;

    public void initialize() {
        int userId = AuthController.currentUserId; // Récupère l'ID utilisateur connecté
        if (userId != 0) {
            loadUserProfile(userId);
        }
    }

    private void loadUserProfile(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usernameLabel.setText(rs.getString("username"));
                emailLabel.setText(rs.getString("email"));
                nomLabel.setText(rs.getString("nom"));
                prenomLabel.setText(rs.getString("prenom"));
                dateNaissanceLabel.setText(rs.getString("date_naissance"));
                adresseLabel.setText(rs.getString("adresse"));
                telephoneLabel.setText(rs.getString("telephone"));
                professionLabel.setText(rs.getString("profession"));
                nationaliteLabel.setText(rs.getString("nationalite"));
                languesLabel.setText(rs.getString("langues"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }
}