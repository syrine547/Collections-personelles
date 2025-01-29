package Service;

import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthService {

    // Méthode pour ajouter un utilisateur
    public boolean addUser(String username, String password, String email, String nom, String prenom, String dateNaissance, String adresse, String telephone, String profession, String nationalite, String langues) {
        String query = "INSERT INTO Collections.users (username, password, email, nom, prenom, date_naissance, adresse, telephone, profession, nationalite, langues) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getCon();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Remplir les paramètres de la requête avec les valeurs passées à la méthode
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, nom);
            preparedStatement.setString(5, prenom);
            preparedStatement.setString(6, dateNaissance);
            preparedStatement.setString(7, adresse);
            preparedStatement.setString(8, telephone);
            preparedStatement.setString(9, profession);
            preparedStatement.setString(10, nationalite);
            preparedStatement.setString(11, langues);

            // Retourne vrai si l'ajout a réussi
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour supprimer un utilisateur
    public boolean deleteUser(String username) {
        String query = "DELETE FROM Collections.users WHERE username = ?";
        try (Connection connection = DataSource.getInstance().getCon();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            return preparedStatement.executeUpdate() > 0; // Retourne vrai si suppression réussie
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
