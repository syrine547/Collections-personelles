package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class GestionLivresController {

    @FXML
    public void handleAjouter() {
        afficherMessage("Ajouter", "Le bouton Ajouter a été cliqué.");
    }

    @FXML
    public void handleModifier() {
        afficherMessage("Modifier", "Le bouton Modifier a été cliqué.");
    }

    @FXML
    public void handleSupprimer() {
        afficherMessage("Supprimer", "Le bouton Supprimer a été cliqué.");
    }

    // Méthode utilitaire pour afficher des alertes
    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
