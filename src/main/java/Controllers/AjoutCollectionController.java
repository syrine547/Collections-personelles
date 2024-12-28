package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import javafx.scene.control.Alert.AlertType;


public class AjoutCollectionController {
    @FXML
    private VBox vboxAttributs;

    @FXML
    private void handleAjouterAttribut() {
        HBox hbox = new HBox(10);
        TextField fieldAttribut = new TextField();
        fieldAttribut.setPromptText("Nom de l'attribut");
        ComboBox<String> comboType = new ComboBox<>();
        comboType.getItems().addAll("VARCHAR(255)", "INT", "DATE", "FLOAT"); // Types d'attribut
        comboType.setPromptText("Type");
        hbox.getChildren().addAll(fieldAttribut, comboType);
        vboxAttributs.getChildren().add(hbox);
    }

    @FXML
    private TextField fieldNomCollection;

    @FXML
    private TextArea fieldDescription;

    @FXML
    private TextField fieldObjectifTotal;

    @FXML
    private void handleCreerCollection() {
        String nomCollection = fieldNomCollection.getText();
        String description = fieldDescription.getText();

        //String nomCollection = fieldNomCollection.getText().trim();
        int objectifTotal = 0;

        try {
            objectifTotal = Integer.parseInt(fieldObjectifTotal.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide pour l'objectif total.");
            return;
        }
        // Validation du nom de la collection
        if (nomCollection == null || nomCollection.trim().isEmpty()) {
            showAlert("Erreur", "Le nom de la collection est obligatoire !");
            return;
        }

        // Construire le SQL pour créer la table
        StringBuilder sql = new StringBuilder("CREATE TABLE Collections." + nomCollection + " (");
        sql.append("id").append(nomCollection).append(" INT AUTO_INCREMENT PRIMARY KEY, "); // Identifiant par défaut

        for (Node node : vboxAttributs.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                TextField fieldAttribut = (TextField) hbox.getChildren().get(0);
                ComboBox<String> comboType = (ComboBox<String>) hbox.getChildren().get(1);

                String nomAttribut = fieldAttribut.getText();
                String typeAttribut = comboType.getValue();

                if (nomAttribut != null && typeAttribut != null) {
                    sql.append(nomAttribut).append(" ").append(typeAttribut).append(", ");
                }
            }
        }

        // Ajouter la date d'ajout par défaut
        sql.append("dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Collections", "root", "MySQL123!");
             Statement stmt = con.createStatement()) {
            // Créer la nouvelle table
            stmt.execute(sql.toString());

            // Ajouter dans typesExistants
            String insertSQL = "INSERT INTO Collections.typesExistants (nomType, description, objectif_total) VALUES (?, ?, ?)";
            try (PreparedStatement pre = con.prepareStatement(insertSQL)) {
                pre.setString(1, nomCollection);
                pre.setString(2, description);
                pre.setInt(3, objectifTotal);

                pre.executeUpdate();
            }

            showAlert("Succès", "Collection ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la création de la collection !"+e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION); // Changez en AlertType.ERROR pour une erreur
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
