package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML de GestionLivres
        Parent root = FXMLLoader.load(getClass().getResource("/GestionLivres.fxml"));

        // Configurer la scène principale
        primaryStage.setTitle("Gestion des Livres");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lancer l'application JavaFX
        launch(args);
    }
}
