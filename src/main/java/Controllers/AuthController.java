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

    // Méthode pour gérer la connexion de l'utilisateur
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
            if (checkEmailExists(email)) {
                sendPasswordResetEmail(email);
                showAlert("Réinitialisation de mot de passe", "Email envoyé", "Un email de réinitialisation a été envoyé à " + email, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Email introuvable", "L'adresse email " + email + " n'est pas enregistrée.", Alert.AlertType.ERROR);
            }
        });
    }

    // Classe interne pour envoyer un email
    public static class EmailService {
        private static final String SENDER_EMAIL = "votre_email@gmail.com"; // Remplacez par votre email
        private static final String SENDER_PASSWORD = "votre_mot_de_passe"; // Remplacez par votre mot de passe d'application

        public static void sendPasswordResetEmail(String recipientEmail) {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Réinitialisation du mot de passe");
                message.setText("Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n\n" +
                        "https://votre_site.com/reinitialisation-mot-de-passe?email=" + recipientEmail);

                Transport.send(message);
                System.out.println("Email de réinitialisation envoyé à : " + recipientEmail);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour envoyer l'email de réinitialisation
    private void sendPasswordResetEmail(String email) {
        EmailService.sendPasswordResetEmail(email);
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
}
