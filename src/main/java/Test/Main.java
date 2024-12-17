package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charge le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("/GestionLivres.fxml"));
            primaryStage.setTitle("Gestion des Livres"); // Titre de la fenêtre
            primaryStage.setScene(new Scene(root, 600, 400)); // Taille de la fenêtre
            primaryStage.show(); // Affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Lancer l'application JavaFX
    }
}
