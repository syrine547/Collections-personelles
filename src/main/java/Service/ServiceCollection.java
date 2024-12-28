package Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Utils.DataSource;

public class ServiceCollection<T> {
    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private String nomTable; // Nom de la table pour cette collection
    private static final int OBJECTIF_TOTAL = 100;

    public ServiceCollection(String nomTable) {
        this.nomTable = nomTable;
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean ajouterElement(String[] colonnes, Object[] valeurs) throws SQLException {
        if (colonnes.length != valeurs.length) {
            throw new IllegalArgumentException("Le nombre de colonnes et de valeurs doit correspondre.");
        }

        // Construire la requête SQL
        StringBuilder query = new StringBuilder("INSERT INTO Collections." + nomTable + " (");
        for (String colonne : colonnes) {
            query.append(colonne).append(",");
        }
        query.deleteCharAt(query.length() - 1).append(") VALUES (");
        for (int i = 0; i < valeurs.length; i++) {
            query.append("?,");
        }
        query.deleteCharAt(query.length() - 1).append(")");

        try (PreparedStatement pre = con.prepareStatement(query.toString())) {
            for (int i = 0; i < valeurs.length; i++) {
                pre.setObject(i + 1, valeurs[i]);
            }
            return pre.executeUpdate() > 0;
        }
    }

    public boolean supprimerElement(String colonneId, int id) throws SQLException {
        String query = "DELETE FROM Collections." + nomTable + " WHERE " + colonneId + " = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, id);
            return pre.executeUpdate() > 0;
        }
    }

    public boolean updateElement(String[] colonnes, Object[] valeurs, String colonneId, int id) throws SQLException {
        if (colonnes.length != valeurs.length) {
            throw new IllegalArgumentException("Le nombre de colonnes et de valeurs doit correspondre.");
        }

        // Construire la requête SQL
        StringBuilder query = new StringBuilder("UPDATE Collections." + nomTable + " SET ");
        for (String colonne : colonnes) {
            query.append(colonne).append(" = ?,");
        }
        query.deleteCharAt(query.length() - 1).append(" WHERE ").append(colonneId).append(" = ?");

        try (PreparedStatement pre = con.prepareStatement(query.toString())) {
            for (int i = 0; i < valeurs.length; i++) {
                pre.setObject(i + 1, valeurs[i]);
            }
            pre.setInt(valeurs.length + 1, id);
            return pre.executeUpdate() > 0;
        }
    }

    public List<T> readAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String query = "SELECT * FROM Collections." + nomTable;

        try (ResultSet resultSet = ste.executeQuery(query)) {
            while (resultSet.next()) {
                // Remplir cette partie avec une logique pour mapper les résultats
                // Cela dépend de la structure des données de vos collections
            }
        }
        return list;
    }

    public int getNombreTotal() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections." + nomTable;
        try (ResultSet resultSet = ste.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        }
        return 0;
    }

    public int getObjectifTotalParNom(String nomCollection) throws SQLException {
        String query = "SELECT objectif_total FROM Collections.typesExistants WHERE nomType = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setString(1, nomCollection);
            ResultSet resultSet = pre.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("objectif_total");
            }
        }
        return 0; // Valeur par défaut
    }
}
