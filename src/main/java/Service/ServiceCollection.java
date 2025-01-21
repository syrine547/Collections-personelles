package Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServiceCollection implements AutoCloseable {

    private final String nomTable;
    private final Connection con;

    public ServiceCollection(String nomTable) throws SQLException {
        this.nomTable = nomTable;
        this.con = DataSource.getInstance().getCon();
        if (this.con == null) {
            throw new SQLException("La connexion à la base de données est nulle.");
        }
    }

    public boolean ajouterElement(Map<String, Object> element) throws SQLException {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("L'élément à ajouter ne peut pas être vide.");
        }

        StringBuilder query = new StringBuilder("INSERT INTO `" + nomTable + "` (");
        StringBuilder values = new StringBuilder("VALUES (");

        // Construire la requête en ignorant 'id' et 'dateAjout'
        for (String colonne : element.keySet()) {
            if (!colonne.equalsIgnoreCase("id") && !colonne.equalsIgnoreCase("dateAjout")) {
                query.append("`").append(colonne).append("`, ");
                values.append("?, ");
            }
        }

        query.delete(query.length() - 2, query.length());
        values.delete(values.length() - 2, values.length());

        query.append(") ").append(values).append(")");

        try (PreparedStatement pre = con.prepareStatement(query.toString())) {
            int i = 1;
            for (String colonne : element.keySet()) {
                if (!colonne.equalsIgnoreCase("id") && !colonne.equalsIgnoreCase("dateAjout")) {
                    pre.setObject(i++, element.get(colonne));
                }
            }
            return pre.executeUpdate() > 0;
        }
    }

    public boolean supprimerElement(int id, String colonneId) throws SQLException {
        String query = "DELETE FROM Collections.`" + nomTable + "` WHERE `" + colonneId + "` = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, id);
            return pre.executeUpdate() > 0;
        }
    }

    public boolean updateElement(int id, String colonneId, Map<String, Object> element) throws SQLException {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("L'élément à mettre à jour ne peut pas être vide.");
        }

        StringBuilder query = new StringBuilder("UPDATE Collections.`" + nomTable + "` SET ");

        for (String colonne : element.keySet()) {
            query.append("`").append(colonne).append("` = ?, ");
        }

        query.delete(query.length() - 2, query.length());

        query.append(" WHERE `").append(colonneId).append("` = ?");

        try (PreparedStatement pre = con.prepareStatement(query.toString())) {
            int i = 1;
            for (Object valeur : element.values()) {
                pre.setObject(i++, valeur);
            }
            pre.setInt(i, id);
            return pre.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> readAll() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT * FROM Collections.`" + nomTable + "`";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                list.add(row);
            }
        }
        return list;
    }

    public List<String> getAttributs() throws SQLException {
        List<String> attributs = new ArrayList<>();
        String query = "SELECT * FROM Collections.`" + nomTable + "` LIMIT 1";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                attributs.add(metaData.getColumnName(i));
            }
        }
        return attributs;
    }

    public boolean supprimerCollection(String nomCollection) throws SQLException {
        String query = "DROP TABLE IF EXISTS Collections.`" + nomCollection + "`";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
        String deleteMeta = "DELETE FROM Collections.typesExistants WHERE nomType = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteMeta)) {
            pstmt.setString(1, nomCollection);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean renommerCollection(String ancienNom, String nouveauNom) throws SQLException {
        String query = "ALTER TABLE Collections.`" + ancienNom + "` RENAME TO Collections.`" + nouveauNom + "`";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
        String updateMeta = "UPDATE Collections.typesExistants SET nomType = ? WHERE nomType = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateMeta)) {
            pstmt.setString(1, nouveauNom);
            pstmt.setString(2, ancienNom);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void mettreAJourObjectif(String nomCollection, int nouvelObjectif) throws SQLException {
        String query = "UPDATE Collections.typesExistants SET objectif_total = ? WHERE nomType = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, nouvelObjectif);
            pstmt.setString(2, nomCollection);
            pstmt.executeUpdate();
        }
    }

    public int getObjectifTotalParNom(String nomCollection) throws SQLException {
        String query = "SELECT objectif_total FROM Collections.typesExistants WHERE nomType = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, nomCollection);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("objectif_total");
                }
            }
        }
        return 0; // Retourne 0 si l'objectif n'est pas trouvé
    }

    public ObservableList<Map<String, String>> getAttributsAvecTypes() throws SQLException {
        ObservableList<Map<String, String>> attributs = FXCollections.observableArrayList();
        String query = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'Collections' AND TABLE_NAME = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, nomTable); // `nomTable` est la table associée à la collection
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> attribut = new HashMap<>();
                    attribut.put("nom", rs.getString("COLUMN_NAME"));
                    attribut.put("type", rs.getString("DATA_TYPE"));
                    attributs.add(attribut);
                }
            }
        }
        return attributs;
    }
    public void synchroniserAttributs(String nomTable, ObservableList<Map<String, String>> attributs) throws SQLException {
        // Récupérer les attributs existants dans la table
        List<Map<String, String>> attributsExistants = getAttributsAvecTypes();

        try (Statement stmt = con.createStatement()) {
            // Supprimer les colonnes inexistantes
            for (Map<String, String> existant : attributsExistants) {
                boolean existeEncore = attributs.stream().anyMatch(a -> a.get("nom").equals(existant.get("nom")));
                if (!existeEncore) {
                    String query = "ALTER TABLE `" + nomTable + "` DROP COLUMN `" + existant.get("nom") + "`";
                    stmt.executeUpdate(query);
                }
            }

            // Ajouter ou modifier les colonnes
            for (Map<String, String> attribut : attributs) {
                String nomAttribut = attribut.get("nom");
                String typeAttribut = attribut.get("type");

                if (nomAttribut == null || typeAttribut == null) {
                    throw new IllegalArgumentException("Nom ou type d'attribut manquant");
                }

                // Vérifier si l'attribut existe déjà
                boolean existe = attributsExistants.stream()
                        .anyMatch(e -> e.get("nom").equals(nomAttribut));

                if (!existe) {
                    // Ajouter une nouvelle colonne
                    String query = "ALTER TABLE `" + nomTable + "` ADD COLUMN `" + nomAttribut + "` " + typeAttribut;
                    stmt.executeUpdate(query);
                } else {
                    // Modifier une colonne existante si le type a changé
                    for (Map<String, String> existant : attributsExistants) {
                        if (existant.get("nom").equals(nomAttribut)) {
                            if (!existant.get("type").equals(typeAttribut)) {
                                String query = "ALTER TABLE `" + nomTable + "` MODIFY COLUMN `" + nomAttribut + "` " + typeAttribut;
                                stmt.executeUpdate(query);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() throws SQLException {
        // Rien à faire ici car on utilise try-with-resources dans les autres méthodes.
    }
}