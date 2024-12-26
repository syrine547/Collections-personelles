package Service;

import Entity.CartePostale;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCartePostale implements IServiceCartePostale<CartePostale>, ServiceStatistique {

    private final Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 70;

    public ServiceCartePostale() {
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterCartePostale(CartePostale cartePostale) throws SQLException {
        String query = "INSERT INTO Collections.CartePostale (titreCartePostale, quantité) VALUES (?, ?)";
        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pre = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pre.setString(1, cartePostale.getTitreCartePostale());
            pre.setInt(2, cartePostale.getQuantite()); // Gestion de la quantité

            int res = pre.executeUpdate();
            if (res > 0) {
                // Récupérer l'ID généré automatiquement
                ResultSet generatedKeys = pre.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    cartePostale.setIdCartePostale(generatedId);
                    System.out.println("Carte postale ajoutée avec ID : " + generatedId);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supprimerCartePostale(CartePostale cartePostale) throws SQLException {
        String query = "DELETE FROM Collections.CartePostale WHERE idCartePostale = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, cartePostale.getIdCartePostale());
            int res = pre.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean updateCartePostale(CartePostale cartePostale) throws SQLException {
        String query = "UPDATE Collections.CartePostale SET titreCartePostale = ?, quantité = ? WHERE idCartePostale = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setString(1, cartePostale.getTitreCartePostale());
            pre.setInt(2, cartePostale.getQuantite()); // Mise à jour de la quantité
            pre.setInt(3, cartePostale.getIdCartePostale());

            int res = pre.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public CartePostale findById(int id) throws SQLException {
        String query = "SELECT * FROM Collections.CartePostale WHERE idCartePostale = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, id);

            ResultSet resultSet = pre.executeQuery();
            if (resultSet.next()) {
                int idCartePostale = resultSet.getInt("idCartePostale");
                String titreCartePostale = resultSet.getString("titreCartePostale");
                int quantite = resultSet.getInt("quantite");

                return new CartePostale(idCartePostale, titreCartePostale, quantite);
            }
        }
        return null;
    }

    @Override
    public List<CartePostale> readALL() throws SQLException {
        List<CartePostale> list = new ArrayList<>();
        String query = "SELECT * FROM Collections.CartePostale";

        try (ResultSet resultSet = ste.executeQuery(query)) {
            while (resultSet.next()) {
                int idCartePostale = resultSet.getInt("idCartePostale");
                String titreCartePostale = resultSet.getString("titreCartePostale");
                int quantite = resultSet.getInt("quantite");

                CartePostale cartePostale = new CartePostale(idCartePostale, titreCartePostale, quantite);
                list.add(cartePostale);
            }
        }
        return list;
    }

    @Override
    public int getNombreTotal() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections.CartePostale";
        try (ResultSet resultSet = ste.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        }
        return 0;
    }

    @Override
    public int getObjectifTotal() {
        return OBJECTIF_TOTAL;
    }
}
