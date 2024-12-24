package Service;

import java.util.ArrayList;
import java.util.List;
import Entity.Livre;
import Utils.DataSource;

import java.sql.*;

public class ServiceLivre implements IServiceLivre<Livre>, ServiceStatistique {
    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 100;

    public ServiceLivre() {
        try {
            ste = con.createStatement();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterLivre(Livre livre) throws SQLException {
        String query = "INSERT INTO Collections.Livres (titreLivre, auteurLivre, quantité) VALUES (?, ?, ?)";
        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pre = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pre.setString(1, livre.getTitreLivre());
            pre.setString(2, livre.getAuteurLivre());
            pre.setInt(3, livre.getQuantite());  // Ajoutez la quantité ici

            int res = pre.executeUpdate();
            if (res > 0) {
                // Récupérer l'ID généré automatiquement
                ResultSet generatedKeys = pre.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    livre.setIdLivre(generatedId);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supprimerLivre(Livre livre) throws SQLException {
        String req = "DELETE FROM Collections.Livres WHERE idLivre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, livre.getIdLivre());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean updateLivre(Livre livre) throws SQLException {
        String req = "UPDATE Collections.Livres SET titreLivre = ?, auteurLivre = ? WHERE idLivre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, livre.getTitreLivre());
        pre.setString(2, livre.getAuteurLivre());
        pre.setInt(3, livre.getIdLivre());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public Livre findById(int id) throws SQLException {
        String req = "SELECT * FROM Collections.Livres WHERE idLivre = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);

        ResultSet resultSet = pre.executeQuery();
        if (resultSet.next()) {
            int idLivre = resultSet.getInt("idLivre");
            String titreLivre = resultSet.getString("titreLivre");
            String auteurLivre = resultSet.getString("auteurLivre");

            return new Livre(idLivre, titreLivre, auteurLivre);
        }
        return null;
    }

    @Override
    public List<Livre> readALL() throws SQLException {
        List<Livre> list = new ArrayList<>();
        String req = "SELECT * FROM Collections.Livres";

        ResultSet resultSet = ste.executeQuery(req);
        while (resultSet.next()) {
            int idLivre = resultSet.getInt("idLivre");
            String titreLivre = resultSet.getString("titreLivre");
            String auteurLivre = resultSet.getString("auteurLivre");

            Livre livre = new Livre(idLivre, titreLivre, auteurLivre);
            list.add(livre);
        }
        return list;
    }

    // Nombre total de livres
    @Override
    public int getNombreTotal() throws SQLException {
        String req = "SELECT COUNT(*) AS total FROM Collections.Livres";
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
