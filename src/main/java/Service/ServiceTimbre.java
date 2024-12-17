package Service;

import Entity.Timbre;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTimbre implements IServiceTimbre<Timbre>, ServiceStatistique {

    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 5;

    public ServiceTimbre() {
        try {
            ste = con.createStatement();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterTimbre(Timbre timbre) throws SQLException {
        // Ajouter Statement.RETURN_GENERATED_KEYS pour demander les clés générées
        PreparedStatement pre = con.prepareStatement(
                "INSERT INTO Collections.Timbres (nomTimbre) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
        );
        pre.setString(1, timbre.getNomTimbre());

        int res = pre.executeUpdate();
        if (res > 0) {
            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = pre.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1); // Premier champ = l'ID généré
                timbre.setIdTimbre(generatedId); // Mettre à jour l'objet Livre avec l'ID généré
                System.out.println("Timbre ajouté avec ID : " + generatedId);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean supprimerTimbre(Timbre timbre) throws SQLException {
        String req = "DELETE FROM Collections.Timbres WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, timbre.getIdTimbre());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean updateTimbre(Timbre timbre) throws SQLException {
        String req = "UPDATE Collections.Timbres SET nomTimbre = ? WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, timbre.getNomTimbre());
        pre.setInt(2, timbre.getIdTimbre());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public Timbre findById(int id) throws SQLException {
        String req = "SELECT * FROM Collections.Timbres WHERE idTimbre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);

        ResultSet resultSet = pre.executeQuery();
        if (resultSet.next()) {
            int idTimbre = resultSet.getInt("idTimbre");
            String nomTimbre = resultSet.getString("nomTimbre");

            return new Timbre(idTimbre, nomTimbre);
        }
        return null;
    }

    @Override
    public List<Timbre> readALL() throws SQLException {
        List<Timbre> list = new ArrayList<>();
        String req = "SELECT * FROM Collections.Timbres";

        ResultSet resultSet = ste.executeQuery(req);
        while (resultSet.next()) {
            int idTimbre = resultSet.getInt("idTimbre");
            String nomTimbre = resultSet.getString("nomTimbre");

            Timbre timbre = new Timbre(idTimbre, nomTimbre);
            list.add(timbre);
        }
        return list;
    }

    // Nombre total de Timbre
    public int getNombreTotal() throws SQLException {
        String req = "SELECT COUNT(*) AS total FROM Collections.Timbres";
        ResultSet resultSet = ste.executeQuery(req);
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
