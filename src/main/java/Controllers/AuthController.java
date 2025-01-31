package Controllers;

import Controllers.AjoutCollectionController;

import Utils.DataSource;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import Service.AuthService;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.Optional;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import Service.AuthService;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import static Utils.DataSource.checkEmailExists;

public class AuthController {
    private final AuthService authService;

    @FXML
    private Button forgotPasswordButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    public static int currentUserId;  // Stocke l'ID de l'utilisateur connecté

    public AuthController() {
        this.authService = new AuthService();
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        forgotPasswordButton.setOnAction(e -> onForgotPasswordClick());
        createAccountButton.setOnAction(e -> onCreateAccountClick());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String loginResult = DataSource.checkLogin(username, password);
        if (loginResult != null) {
            String[] parts = loginResult.split(":");
            int userId = Integer.parseInt(parts[0]);
            String role = parts[1];
            AuthController.currentUserId = userId;  // Enregistre l'ID utilisateur


            if (userId == 0) {
                showAlert("Erreur", "Erreur","L'utilisateur spécifié n'existe pas.", Alert.AlertType.INFORMATION);
                return;
            }

            showAlert("Connexion réussie", "Bienvenue " + username, "Vous êtes connecté en tant que " + role, Alert.AlertType.INFORMATION);
            navigateToDashboard(userId);
        } else {
            showAlert("Erreur de connexion", "Nom d'utilisateur ou mot de passe incorrect.", "", Alert.AlertType.ERROR);
        }
    }
    // Méthode pour naviguer vers le tableau de bord
    private void navigateToDashboard(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du Dashboard
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserId(userId);  // Passe l'ID utilisateur au Dashboard
            }

            // Changer de scène
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Navigation échouée", "Impossible de naviguer vers le Dashboard.", Alert.AlertType.ERROR);
        }
    }


    // Méthode pour afficher des alertes
    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour gérer la réinitialisation du mot de passe
    @FXML
    private void onForgotPasswordClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Réinitialisation de mot de passe");
        dialog.setHeaderText("Mot de passe oublié");
        dialog.setContentText("Veuillez entrer votre adresse e-mail :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            if (DataSource.checkEmailExists(email)) {
                showResetPasswordPage(email);
            } else {
                showAlert("Erreur", "Email introuvable", "L'adresse email " + email + " n'est pas enregistrée.", Alert.AlertType.ERROR);
            }
        });
    }

    private boolean addUser(String username, String password, String email, String nom, String prenom, String dateNaissance, String adresse, String telephone, String profession, String nationalite, String langues) {
        // Appeler la méthode addUser de AuthService pour ajouter un utilisateur
        return authService.addUser(username, password, email, nom, prenom, dateNaissance, adresse, telephone, profession, nationalite, langues);
    }


    // Méthode pour créer un compte
    private void onCreateAccountClick() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer un compte");
        dialog.setHeaderText("Remplissez les informations pour créer un nouveau compte.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        TextField emailField = new TextField();
        emailField.setPromptText("Adresse email");

        // Additional fields
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField dateNaissanceField = new TextField();
        dateNaissanceField.setPromptText("Date de naissance");

        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse");

        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");

        TextField professionField = new TextField();
        professionField.setPromptText("Profession");

        TextField nationaliteField = new TextField();
        nationaliteField.setPromptText("Nationalité");

        TextField languesField = new TextField();
        languesField.setPromptText("Langues parlées");

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(usernameField, passwordField, emailField, nomField, prenomField, dateNaissanceField, adresseField, telephoneField, professionField, nationaliteField, languesField);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String dateNaissance = dateNaissanceField.getText();
            String adresse = adresseField.getText();
            String telephone = telephoneField.getText();
            String profession = professionField.getText();
            String nationalite = nationaliteField.getText();
            String langues = languesField.getText();

            // Vérifiez si les champs sont remplis
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || dateNaissance.isEmpty() || adresse.isEmpty() || telephone.isEmpty() || profession.isEmpty() || nationalite.isEmpty() || langues.isEmpty()) {
                showAlert("Erreur", "Champs vides", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
                return;
            }

            // Créez un utilisateur
            boolean isAdded = addUser(username, password, email, nom, prenom, dateNaissance, adresse, telephone, profession, nationalite, langues);
            if (isAdded) {
                showAlert("Succès", "Compte créé", "Le compte a été créé avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Échec de création", "Une erreur est survenue lors de la création du compte.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showResetPasswordPage(String email) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Réinitialiser le mot de passe");
        dialog.setHeaderText("Veuillez entrer votre nouveau mot de passe.");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Nouveau mot de passe:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirmer mot de passe:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (newPassword.equals(confirmPassword)) {
                boolean isUpdated = updatePassword(email, newPassword);
                if (isUpdated) {
                    showAlert("Succès", "Mot de passe réinitialisé", "Votre mot de passe a été réinitialisé avec succès.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de la mise à jour", "Une erreur est survenue lors de la mise à jour du mot de passe.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Erreur", "Mots de passe non correspondants", "Les mots de passe ne correspondent pas. Veuillez réessayer.", Alert.AlertType.ERROR);
            }
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        System.out.println("Tentative de mise à jour du mot de passe pour l'email : " + email);
        String sql = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Lignes affectées : " + rowsAffected);

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
