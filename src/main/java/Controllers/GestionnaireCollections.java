package Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionnaireCollections {

    public void afficherCollection(String nomCollection) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCollection.fxml")); // Chemin relatif depuis le resources
            Parent root = loader.load();

            GenericCollectionController controller = loader.getController();
            controller.setNomCollection(nomCollection);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion de la collection : " + nomCollection);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue gestionCollection.fxml : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pour une alerte plus simple
        alert.setContentText(content);
        alert.showAndWait();
    }
}