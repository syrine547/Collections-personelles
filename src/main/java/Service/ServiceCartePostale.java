package Service;

import Entity.CartePostale;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCartePostale implements IServiceCartePostale <CartePostale>, ServiceStatistique {

    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 70;

    public ServiceCartePostale() {
        try {
            ste = con.createStatement();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterCartePostale(CartePostale cartePostale) throws SQLException {
        // Ajouter Statement.RETURN_GENERATED_KEYS pour demander les clés générées
        PreparedStatement pre = con.prepareStatement(
                "INSERT INTO Collections.CartePostale (titreCartePostale) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
        );
        pre.setString(1, cartePostale.getTitreCartePostale());

        int res = pre.executeUpdate();
        if (res > 0) {
            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = pre.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1); // Premier champ = l'ID généré
                cartePostale.setIdCartePostale(generatedId); // Mettre à jour l'objet Livre avec l'ID généré
                System.out.println("Carte postale ajouté avec ID : " + generatedId);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean supprimerCartePostale(CartePostale cartePostale) throws SQLException {
        String req = "DELETE FROM Collections.CartePostale WHERE idCartePostale = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, cartePostale.getIdCartePostale());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean updateCartePostale(CartePostale cartePostale) throws SQLException {
        String req = "UPDATE Collections.CartePostale SET titreCartePostale = ? WHERE idCartePostale = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, cartePostale.getTitreCartePostale());
        pre.setInt(2, cartePostale.getIdCartePostale());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public CartePostale findById(int id) throws SQLException {
        String req = "SELECT * FROM Collections.CartePostale WHERE idCartePostale = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);

        ResultSet resultSet = pre.executeQuery();
        if (resultSet.next()) {
            int idCartePostale = resultSet.getInt("idCartePostale");
            String titreCartePostale = resultSet.getString("titreCartePostale");

            return new CartePostale(idCartePostale, titreCartePostale);
        }
        return null;
    }

    @Override
    public List<CartePostale> readALL() throws SQLException {
        List<CartePostale> list = new ArrayList<>();
        String req = "SELECT * FROM Collections.CartePostale";

        ResultSet resultSet = ste.executeQuery(req);
        while (resultSet.next()) {
            int idCartePostale = resultSet.getInt("idCartePostale");
            String titreCartePostale = resultSet.getString("titreCartePostale");

            CartePostale cartePostale = new CartePostale(idCartePostale, titreCartePostale);
            list.add(cartePostale);
        }
        return list;
    }

    // Nombre total de Cartes Postale
    public int getNombreTotal() throws SQLException {
        String req = "SELECT COUNT(*) AS total FROM Collections.CartePostale";
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
