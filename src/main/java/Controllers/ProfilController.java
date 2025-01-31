package Controllers;

import Utils.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilController {

    @FXML
    private TextField usernameLabel;
    @FXML
    private TextField emailLabel;
    @FXML
    private TextField nomLabel;
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

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField dateNaissanceTextField;
    @FXML
    private TextField adresseTextField;
    @FXML
    private TextField telephoneTextField;
    @FXML
    private TextField professionTextField;
    @FXML
    private TextField nationaliteTextField;
    @FXML
    private TextField languesTextField;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;


    public void initialize() {
        int userId = AuthController.currentUserId; // Récupère l'ID utilisateur connecté
        if (userId != 0) {
            loadUserProfile(userId);
        } else {
            System.out.println("Aucun utilisateur connecté.");
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
            } else {
                System.out.println("Utilisateur non trouvé.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Utilisateur connecté avec l'ID : " + userId);
        // Vous pouvez ajouter ici du code pour charger les données du profil de l'utilisateur
    }

    public int getUserId() {
        return userId;
    }

    public ProfilController() {
        // Constructeur sans argument obligatoire pour JavaFX
    }

    @FXML
    private void handleEditProfile() {

        // Si vous voulez rendre les champs modifiables, remplacez les labels par des TextFields
        usernameLabel.setEditable(true);
        usernameTextField.setVisible(true);
        usernameTextField.setText(usernameLabel.getText());

        emailLabel.setEditable(true);
        emailTextField.setVisible(true);
        emailTextField.setText(emailLabel.getText());

        nomLabel.setEditable(true);
        nomTextField.setVisible(true);
        nomTextField.setText(nomLabel.getText());

        prenomLabel.setVisible(false);
        prenomTextField.setVisible(true);
        prenomTextField.setText(prenomLabel.getText());

        // Ajoutez les autres labels et textFields ici de la même manière.

        // Masquez le bouton "Modifier" pour ne plus pouvoir cliquer dessus
        editButton.setVisible(false);
    }

    @FXML
    private void handleSaveProfile() {
        // Récupérer les nouvelles informations saisies par l'utilisateur
        String newUsername = usernameTextField.getText();
        String newEmail = emailTextField.getText();
        String newNom = nomTextField.getText();
        String newPrenom = prenomTextField.getText();
        String newDateNaissance = dateNaissanceTextField.getText();  // Assurez-vous de gérer tous les champs
        String newAdresse = adresseTextField.getText();
        String newTelephone = telephoneTextField.getText();
        String newProfession = professionTextField.getText();
        String newNationalite = nationaliteTextField.getText();
        String newLangues = languesTextField.getText();

        // Effectuer la mise à jour dans la base de données (exemple avec SQL)
        String updateQuery = "UPDATE users SET username = ?, email = ?, nom = ?, prenom = ?, date_naissance = ?, adresse = ?, " +
                "telephone = ?, profession = ?, nationalite = ?, langues = ? WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            // Paramètres de mise à jour
            stmt.setString(1, newUsername);
            stmt.setString(2, newEmail);
            stmt.setString(3, newNom);
            stmt.setString(4, newPrenom);
            stmt.setString(5, newDateNaissance);
            stmt.setString(6, newAdresse);
            stmt.setString(7, newTelephone);
            stmt.setString(8, newProfession);
            stmt.setString(9, newNationalite);
            stmt.setString(10, newLangues);
            stmt.setInt(11, AuthController.currentUserId); // Assurez-vous de récupérer l'ID de l'utilisateur connecté

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profil mis à jour avec succès.");
            } else {
                System.out.println("Erreur lors de la mise à jour du profil.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Revenir aux labels après sauvegarde
        usernameLabel.setText(newUsername);
        emailLabel.setText(newEmail);
        nomLabel.setText(newNom);
        prenomLabel.setText(newPrenom);
        dateNaissanceLabel.setText(newDateNaissance);
        adresseLabel.setText(newAdresse);
        telephoneLabel.setText(newTelephone);
        professionLabel.setText(newProfession);
        nationaliteLabel.setText(newNationalite);
        languesLabel.setText(newLangues);

        // Masquer les TextFields et rendre les labels visibles
        usernameLabel.setVisible(true);
        emailLabel.setVisible(true);
        nomLabel.setVisible(true);
        prenomLabel.setVisible(true);
        dateNaissanceLabel.setVisible(true);
        adresseLabel.setVisible(true);
        telephoneLabel.setVisible(true);
        professionLabel.setVisible(true);
        nationaliteLabel.setVisible(true);
        languesLabel.setVisible(true);

        usernameTextField.setVisible(false);
        emailTextField.setVisible(false);
        nomTextField.setVisible(false);
        prenomTextField.setVisible(false);
        dateNaissanceTextField.setVisible(false);
        adresseTextField.setVisible(false);
        telephoneTextField.setVisible(false);
        professionTextField.setVisible(false);
        nationaliteTextField.setVisible(false);
        languesTextField.setVisible(false);

        // Réactiver le bouton "Modifier" et masquer "Enregistrer" et "Annuler"
        saveButton.setVisible(false);
    }

    @FXML
    private void handleCancelEdit() {
        // Annuler l'édition et revenir à l'état initial (en utilisant les valeurs actuelles des labels)
        usernameTextField.setVisible(false);
        emailTextField.setVisible(false);
        nomTextField.setVisible(false);
        prenomTextField.setVisible(false);
        dateNaissanceTextField.setVisible(false);
        adresseTextField.setVisible(false);
        telephoneTextField.setVisible(false);
        professionTextField.setVisible(false);
        nationaliteTextField.setVisible(false);
        languesTextField.setVisible(false);

        // Revenir aux labels visibles avec les anciennes valeurs
        usernameLabel.setVisible(true);
        emailLabel.setVisible(true);
        nomLabel.setVisible(true);
        prenomLabel.setVisible(true);
        dateNaissanceLabel.setVisible(true);
        adresseLabel.setVisible(true);
        telephoneLabel.setVisible(true);
        professionLabel.setVisible(true);
        nationaliteLabel.setVisible(true);
        languesLabel.setVisible(true);

        // Réinitialiser les TextFields avec les anciennes valeurs
        usernameTextField.setText(usernameLabel.getText());
        emailTextField.setText(emailLabel.getText());
        nomTextField.setText(nomLabel.getText());
        prenomTextField.setText(prenomLabel.getText());
        dateNaissanceTextField.setText(dateNaissanceLabel.getText());
        adresseTextField.setText(adresseLabel.getText());
        telephoneTextField.setText(telephoneLabel.getText());
        professionTextField.setText(professionLabel.getText());
        nationaliteTextField.setText(nationaliteLabel.getText());
        languesTextField.setText(languesLabel.getText());

        // Réactiver le bouton "Modifier" et masquer "Enregistrer" et "Annuler"
        cancelButton.setVisible(false);
    }


}
