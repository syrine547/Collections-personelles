package Service;
import java.util.ArrayList;
import java.util.List;
import Entity.Livre;
import Utils.DataSource;

import java.sql.*;

public class ServiceLivre implements IServiceLivre<Livre> {
    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;

    public ServiceLivre() {
        try {
            ste = con.createStatement();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterLivre(Livre livre) throws SQLException {
        // Ajouter Statement.RETURN_GENERATED_KEYS pour demander les clés générées
        PreparedStatement pre = con.prepareStatement(
                "INSERT INTO Collections.Livres (titreLivre, auteurLivre) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        pre.setString(1, livre.getTitreLivre());
        pre.setString(2, livre.getAuteurLivre());

        int res = pre.executeUpdate();
        if (res > 0) {
            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = pre.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1); // Premier champ = l'ID généré
                livre.setIdLivre(generatedId); // Mettre à jour l'objet Livre avec l'ID généré
                System.out.println("Livre ajouté avec ID : " + generatedId);
            }
            return true;
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
}
