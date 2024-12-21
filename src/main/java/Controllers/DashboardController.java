package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    public void handleGestionLivres() {
        chargerVue("GestionLivres.fxml", "Gestion des Livres");
    }

    @FXML
    public void handleGestionPiecesMonnaie() {
        chargerVue("GestionPiecesMonnaie.fxml", "Gestion des Pièces de Monnaie");
    }

    @FXML
    public void handleGestionTimbres() {
        chargerVue("GestionTimbres.fxml", "Gestion des Timbres");
    }

    @FXML
    public void handleGestionCartes() {
        chargerVue("GestionCartes.fxml", "Gestion des Cartes Postales");
    }

    private void chargerVue(String fxml, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/" + fxml));
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
