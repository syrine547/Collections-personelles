package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

import javafx.scene.Node;
import Utils.DataSource;

public class AjoutCollectionController {

    private int userId;  // Déclarer la variable userId

    @FXML
    private VBox vboxAttributs;
    @FXML
    private TextField fieldNomCollection;
    @FXML
    private TextArea fieldDescription;
    @FXML
    private TextField fieldObjectifTotal;

    // Setter pour définir l'ID de l'utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
    }

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

            // Vérifier si l'utilisateur existe dans la table `users`
            System.out.println("userId: " + userId);  // Pour vérifier la valeur de userId
            String checkUserSQL = "SELECT COUNT(*) FROM users WHERE id = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkUserSQL)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("userId: " + userId);  // Pour vérifier la valeur de userId

                    showAlert("Erreur", "L'utilisateur spécifié n'existe pas.");
                    return;
                }
            }

            // Créer la table `Collections`
            stmt.executeUpdate(sql.toString()); // Utiliser executeUpdate pour les requêtes DDL

            // Insérer dans `typesExistants` avec l'ID utilisateur
            String query = "INSERT INTO Collections.typesExistants (nomType, description, objectif_total, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, nomCollection);
            pstmt.setString(2, description);
            pstmt.setInt(3, objectifTotal);
            pstmt.setInt(4, userId); // Utilisation de userId ici
            pstmt.executeUpdate();

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
