package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.Node;
import Utils.DataSource;

public class AjoutCollectionController {

    @FXML
    private VBox vboxAttributs;
    @FXML
    private TextField fieldNomCollection;
    @FXML
    private TextArea fieldDescription;
    @FXML
    private TextField fieldObjectifTotal;

    @FXML
    private void handleAjouterAttribut() {
        HBox hbox = new HBox(10);
        TextField fieldAttribut = new TextField();
        fieldAttribut.setPromptText("Nom de l'attribut");
        ComboBox<String> comboType = new ComboBox<>();
        comboType.getItems().addAll("VARCHAR(255)", "INT", "DATE", "FLOAT", "TEXT");
        comboType.setPromptText("Type");
        hbox.getChildren().addAll(fieldAttribut, comboType);
        vboxAttributs.getChildren().add(hbox);
    }

    @FXML
    private void handleCreerCollection() {
        String nomCollection = fieldNomCollection.getText().trim();
        String description = fieldDescription.getText();
        String objectifTotalStr = fieldObjectifTotal.getText().trim();
        int objectifTotal = 0;

        // Validation
        if (nomCollection.isEmpty()) {
            showAlert("Erreur", "Le nom de la collection est obligatoire.");
            return;
        }

        if (nomCollection.matches(".*\\s.*")) {
            showAlert("Erreur", "Le nom de la collection ne doit pas contenir d'espaces.");
            return;
        }

        try {
            objectifTotal = Integer.parseInt(objectifTotalStr);
            if (objectifTotal < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'objectif total doit être un nombre entier positif.");
            return;
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE Collections.`" + nomCollection + "` (");
        sql.append("`id` INT AUTO_INCREMENT PRIMARY KEY, ");

        boolean attributesAdded = false;

        for (Node node : vboxAttributs.getChildren()) { // Loop only through the dynamically added attributes
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                TextField fieldAttribut = (TextField) hbox.getChildren().get(0);
                ComboBox<String> comboType = (ComboBox<String>) hbox.getChildren().get(1);

                String nomAttribut = fieldAttribut.getText().trim(); // Trim les espaces
                String typeAttribut = comboType.getValue();

                if (nomAttribut.isEmpty() || typeAttribut == null) {
                    showAlert("Erreur", "Veuillez remplir tous les champs d'attribut.");
                    return;
                }
                // Validation des noms d'attributs pour éviter les erreurs SQL
                if (nomAttribut.matches(".*[`'\"\\\\;].*")) { // Caractères interdits
                    showAlert("Erreur", "Les noms d'attribut ne doivent pas contenir les caractères : ` ' \" \\ ;");
                    return;
                }
                sql.append("`").append(nomAttribut).append("` ").append(typeAttribut).append(", ");
                attributesAdded = true;
            }
        }

        if (attributesAdded) {
            sql.delete(sql.length() - 2, sql.length()); // Supprimer la dernière virgule et l'espace
        }
        sql.append(", `dateAjout` TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        try (Connection con = DataSource.getInstance().getCon();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql.toString()); // Utiliser executeUpdate pour les requêtes DDL

            String insertSQL = "INSERT INTO Collections.typesExistants (nomType, description, objectif_total) VALUES (?, ?, ?)";
            try (PreparedStatement pre = con.prepareStatement(insertSQL)) {
                pre.setString(1, nomCollection);
                pre.setString(2, description);
                pre.setInt(3, objectifTotal);
                pre.executeUpdate();
            }

            showAlert("Succès", "Collection ajoutée avec succès !");
            fieldNomCollection.clear();
            fieldDescription.clear();
            fieldObjectifTotal.clear();
            vboxAttributs.getChildren().clear();
            handleAjouterAttribut();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Échec de la création de la collection : " + e.getMessage()); // Message d'erreur plus précis
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}