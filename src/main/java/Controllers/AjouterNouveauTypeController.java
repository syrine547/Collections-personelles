package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AjouterNouveauTypeController {

    @FXML
    private TextField fieldNomType;

    @FXML
    private TextField fieldDescription;

    // Liste temporaire ou connectez-vous à votre système de données
    private static List<String> typesExistants = new ArrayList<>();

    @FXML
    private void handleAjouter() {
        String nomType = fieldNomType.getText().trim();
        String description = fieldDescription.getText().trim();

        if (nomType.isEmpty() || description.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        // Vérifiez si le type existe déjà
        if (typesExistants.contains(nomType)) {
            System.out.println("Ce type existe déjà !");
            return;
        }

        // Ajoutez le type à la liste des types existants
        typesExistants.add(nomType);
        System.out.println("Type ajouté : " + nomType);

        // Ajoutez également dans votre base de données, si nécessaire

        // Fermez la fenêtre
        Stage stage = (Stage) fieldNomType.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAnnuler() {
        // Fermez la fenêtre
        Stage stage = (Stage) fieldNomType.getScene().getWindow();
        stage.close();
    }
}
