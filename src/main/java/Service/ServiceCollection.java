package Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.DataSource;

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

        StringBuilder query = new StringBuilder("INSERT INTO Collections.`" + nomTable + "` (");
        StringBuilder values = new StringBuilder("VALUES (");

        for (String colonne : element.keySet()) {
            query.append("`").append(colonne).append("`, ");
            values.append("?, ");
        }

        query.delete(query.length() - 2, query.length());
        values.delete(values.length() - 2, values.length());

        query.append(") ").append(values).append(")");

        try (PreparedStatement pre = con.prepareStatement(query.toString())) {
            int i = 1;
            for (Object valeur : element.values()) {
                pre.setObject(i++, valeur);
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

    @Override
    public void close() throws SQLException {
        // Rien à faire ici car on utilise try-with-resources dans les autres méthodes.
    }
}