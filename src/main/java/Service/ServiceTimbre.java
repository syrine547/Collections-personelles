package Service;

import Entity.Timbre;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTimbre implements IServiceTimbre<Timbre>, ServiceStatistique {

    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 100;

    public ServiceTimbre() {
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterTimbre(Timbre timbre) throws SQLException {
        String query = "INSERT INTO Collections.Timbres (nomTimbre, quantité) VALUES (?, ?)";
        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pre = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pre.setString(1, timbre.getNomTimbre());
            pre.setInt(2, timbre.getQuantite()); // Inclure la quantité

            int res = pre.executeUpdate();
            if (res > 0) {
                ResultSet generatedKeys = pre.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // Récupérer l'ID généré
                    timbre.setIdTimbre(generatedId); // Mettre à jour l'objet Timbre avec l'ID généré
                    System.out.println("Timbre ajouté avec ID : " + generatedId);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supprimerTimbre(Timbre timbre) throws SQLException {
        String query = "DELETE FROM Collections.Timbres WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(query);
        pre.setInt(1, timbre.getIdTimbre());
        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean updateTimbre(Timbre timbre) throws SQLException {
        String query = "UPDATE Collections.Timbres SET nomTimbre = ?, quantité = ? WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(query);
        pre.setString(1, timbre.getNomTimbre());
        pre.setInt(2, timbre.getQuantite()); // Mettre à jour la quantité
        pre.setInt(3, timbre.getIdTimbre());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public Timbre findById(int id) throws SQLException {
        String query = "SELECT * FROM Collections.Timbres WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(query);
        pre.setInt(1, id);
        ResultSet resultSet = pre.executeQuery();

        if (resultSet.next()) {
            int idTimbre = resultSet.getInt("idTimbre");
            String nomTimbre = resultSet.getString("nomTimbre");
            int quantite = resultSet.getInt("quantité"); // Récupérer la quantité

            return new Timbre(idTimbre, nomTimbre, quantite);
        }
        return null;
    }

    @Override
    public List<Timbre> readALL() throws SQLException {
        List<Timbre> list = new ArrayList<>();
        String query = "SELECT * FROM Collections.Timbres";
        ResultSet resultSet = ste.executeQuery(query);
        while (resultSet.next()) {
            int idTimbre = resultSet.getInt("idTimbre");
            String nomTimbre = resultSet.getString("nomTimbre");
            int quantite = resultSet.getInt("quantité"); // Récupérer la quantité

            Timbre timbre = new Timbre(idTimbre, nomTimbre, quantite);
            list.add(timbre);
        }
        return list;
    }

    @Override
    public int getNombreTotal() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections.Timbres";
        ResultSet resultSet = ste.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("total");
        }
        return 0;
    }

    @Override
    public int getObjectifTotal() {
        return OBJECTIF_TOTAL;
    }
}
