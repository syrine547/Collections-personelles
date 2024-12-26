package Service;

import java.util.ArrayList;
import java.util.List;
import Entity.PieceMonnaie;
import Utils.DataSource;

import java.sql.*;

public class ServicePieceMonnaie implements IServicePieceMonnaie<PieceMonnaie>, ServiceStatistique {

    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;
    private static final int OBJECTIF_TOTAL = 80;

    public ServicePieceMonnaie() {
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterPieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        String query = "INSERT INTO Collections.PiecesMonnaie (valeurPiecesMonnaie, unitéPiecesMonnaie, quantité) VALUES (?, ?, ?)";
        try (Connection con = DataSource.getInstance().getCon();
             PreparedStatement pre = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pre.setString(1, pieceMonnaie.getValeurPiecesMonnaie());
            pre.setString(2, pieceMonnaie.getUnitéPiecesMonnaie());
            pre.setInt(3, pieceMonnaie.getQuantite());

            int res = pre.executeUpdate();
            if (res > 0) {
                ResultSet generatedKeys = pre.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    pieceMonnaie.setIdPiecesMonnaie(generatedId);
                    System.out.println("Pièce monnaie ajoutée avec ID : " + generatedId);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supprimerPieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        String query = "DELETE FROM Collections.PiecesMonnaie WHERE idPiecesMonnaie = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, pieceMonnaie.getIdPiecesMonnaie());
            int res = pre.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean updatePieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        String query = "UPDATE Collections.PiecesMonnaie SET valeurPiecesMonnaie = ?, unitéPiecesMonnaie = ?, quantité = ? WHERE idPiecesMonnaie = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setString(1, pieceMonnaie.getValeurPiecesMonnaie());
            pre.setString(2, pieceMonnaie.getUnitéPiecesMonnaie());
            pre.setInt(3, pieceMonnaie.getQuantite());
            pre.setInt(4, pieceMonnaie.getIdPiecesMonnaie());

            int res = pre.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public PieceMonnaie findById(int id) throws SQLException {
        String query = "SELECT * FROM Collections.PiecesMonnaie WHERE idPiecesMonnaie = ?";
        try (PreparedStatement pre = con.prepareStatement(query)) {
            pre.setInt(1, id);
            ResultSet resultSet = pre.executeQuery();

            if (resultSet.next()) {
                int idPiecesMonnaie = resultSet.getInt("idPiecesMonnaie");
                String valeurPiecesMonnaie = resultSet.getString("valeurPiecesMonnaie");
                String unitéPiecesMonnaie = resultSet.getString("unitéPiecesMonnaie");
                int quantite = resultSet.getInt("quantité");

                return new PieceMonnaie(idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie, quantite);
            }
        }
        return null;
    }

    @Override
    public List<PieceMonnaie> readALL() throws SQLException {
        List<PieceMonnaie> list = new ArrayList<>();
        String query = "SELECT * FROM Collections.PiecesMonnaie";
        try (ResultSet resultSet = ste.executeQuery(query)) {
            while (resultSet.next()) {
                int idPiecesMonnaie = resultSet.getInt("idPiecesMonnaie");
                String valeurPiecesMonnaie = resultSet.getString("valeurPiecesMonnaie");
                String unitéPiecesMonnaie = resultSet.getString("unitéPiecesMonnaie");
                int quantite = resultSet.getInt("quantité");

                PieceMonnaie pieceMonnaie = new PieceMonnaie(idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie, quantite);
                list.add(pieceMonnaie);
            }
        }
        return list;
    }

    @Override
    public int getNombreTotal() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Collections.PiecesMonnaie";
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
