package Controllers;

import Utils.DataSource;
import javafx.scene.Node;
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
import static Utils.DataSource.checkLogin;

public class AuthController {
    private final AuthService authService;

    @FXML
    private Button btn;

    @FXML
    private TextField txt;

    @FXML
    private Button forgotPasswordButton;

    public AuthController() {
        this.authService = new AuthService();
    }

    // Méthode pour ajouter un utilisateur avec tous les champs
    public boolean addUser(String username, String password, String email, String nom, String prenom, String dateNaissance, String adresse, String telephone, String profession, String nationalite, String langues) {
        // Ajoute un utilisateur avec tous les champs dans la base de données
        return authService.addUser(username, password, email, nom, prenom, dateNaissance, adresse, telephone, profession, nationalite, langues);
    }

    // Méthode pour supprimer un utilisateur
    public boolean deleteUser(String username) {
        return authService.deleteUser(username);
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin());

        // Action pour le bouton "Mot de passe oublié"
        forgotPasswordButton.setOnAction(e -> onForgotPasswordClick());

        createAccountButton.setOnAction(e -> onCreateAccountClick());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Vérification de l'utilisateur et du mot de passe dans la base de données
        if (checkLogin(username, password)) {

            // Naviguer vers le Dashboard après l'authentification réussie
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml")); // Charge le fichier FXML du Dashboard
                Parent root = loader.load();

                // Obtenir la scène actuelle
                Stage stage = (Stage) loginButton.getScene().getWindow(); // Récupère la fenêtre actuelle
                //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Récupère la fenêtre actuelle
                stage.setScene(new Scene(root)); // Définit la nouvelle scène
                stage.setTitle("Dashboard"); // Définit le titre de la fenêtre
                stage.show(); // Affiche la nouvelle scène
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la navigation", "Impossible d'ouvrir le Dashboard.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert(
                    "Erreur de connexion", // Titre
                    "Échec de la connexion", // En-tête
                    "Nom d'utilisateur ou mot de passe incorrect.", // Contenu
                    Alert.AlertType.ERROR // Type d'alerte
            );
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onForgotPasswordClick() {
        // Créez une boîte de dialogue pour demander l'email
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Réinitialisation de mot de passe");
        dialog.setHeaderText("Mot de passe oublié");
        dialog.setContentText("Veuillez entrer votre adresse e-mail :");

        // Récupérez l'email saisi par l'utilisateur
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            // Vérifiez si l'email existe dans la base de données
            if (checkEmailExists(email)) {
                // Si l'email existe, envoyez un lien de réinitialisation
                sendPasswordResetEmail(email);
                showAlert(
                        "Réinitialisation de mot de passe",
                        "Email envoyé",
                        "Un email de réinitialisation a été envoyé à " + email,
                        Alert.AlertType.INFORMATION
                );
            } else {
                // Sinon, affichez un message d'erreur
                showAlert(
                        "Erreur",
                        "Email introuvable",
                        "L'adresse email " + email + " n'est pas enregistrée.",
                        Alert.AlertType.ERROR
                );
            }
        });
    }

    public class EmailService {

        private static final String SENDER_EMAIL = "votre_email@gmail.com"; // Votre adresse email
        private static final String SENDER_PASSWORD = "votre_mot_de_passe"; // Votre mot de passe d'email

        // Méthode pour envoyer un email
        public static void sendPasswordResetEmail(String recipientEmail) {
            // Configuration des propriétés pour Gmail
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            // Authentification avec l'email de l'expéditeur
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                // Création du message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Réinitialisation du mot de passe");
                message.setText("Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n\n" +
                        "https://votre_site.com/reinitialisation-mot-de-passe?email=" + recipientEmail);

                // Envoi de l'email
                Transport.send(message);

                System.out.println("Email de réinitialisation envoyé à : " + recipientEmail);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Simule l'envoi d'un email de réinitialisation de mot de passe.
     * @param email l'adresse email de l'utilisateur
     */
    private void sendPasswordResetEmail(String email) {
        // Appel à la méthode d'envoi d'email
        EmailService.sendPasswordResetEmail(email);
    }

    private void onCreateAccountClick() {
        // Boîte de dialogue pour entrer les informations du nouvel utilisateur
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer un compte");
        dialog.setHeaderText("Remplissez les informations pour créer un nouveau compte.");

        // Champs pour les données utilisateur
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        TextField emailField = new TextField();
        emailField.setPromptText("Adresse email");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField dateNaissanceField = new TextField();
        dateNaissanceField.setPromptText("Date de naissance (ex: 01/01/2000)");

        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse");

        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Numéro de téléphone");

        TextField professionField = new TextField();
        professionField.setPromptText("Profession");

        TextField nationaliteField = new TextField();
        nationaliteField.setPromptText("Nationalité");

        TextArea languesField = new TextArea();
        languesField.setPromptText("Langues parlées");

        // Ajouter les champs dans une VBox
        VBox vbox = new VBox(10); // Espacement entre les champs
        vbox.getChildren().addAll(usernameField, passwordField, emailField, nomField, prenomField, dateNaissanceField, adresseField, telephoneField, professionField, nationaliteField, languesField);

        // Définir le contenu de la boîte de dialogue
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

            // Vérifiez que tous les champs sont remplis
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || dateNaissance.isEmpty() || adresse.isEmpty() || telephone.isEmpty() || profession.isEmpty() || nationalite.isEmpty()) {
                showAlert(
                        "Erreur",
                        "Champs vides",
                        "Veuillez remplir tous les champs.",
                        Alert.AlertType.ERROR
                );
                return;
            }

            // Ajout de l'utilisateur dans la base de données
            boolean isAdded = addUser(username, password, email, nom, prenom, dateNaissance, adresse, telephone, profession, nationalite, langues);
            if (isAdded) {
                showAlert(
                        "Succès",
                        "Compte créé",
                        "Le compte pour " + username + " a été créé avec succès.",
                        Alert.AlertType.INFORMATION
                );
            } else {
                showAlert(
                        "Erreur",
                        "Échec de création",
                        "Une erreur est survenue lors de la création du compte.",
                        Alert.AlertType.ERROR
                );
            }
        }
    }

    public static void main(String[] args) {
        EmailService.sendPasswordResetEmail("Radhouenemonia@gmail.com");
    }
}
