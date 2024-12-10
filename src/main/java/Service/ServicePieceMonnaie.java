package Service;

import java.util.ArrayList;
import java.util.List;
import Entity.PieceMonnaie;
import Utils.DataSource;

import java.sql.*;
public class ServicePieceMonnaie implements IServicePieceMonnaie<PieceMonnaie> {

    private Connection con = DataSource.getInstance().getCon();
    private Statement ste = null;

    public ServicePieceMonnaie() {
        try {
            ste = con.createStatement();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean ajouterPieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        // Ajouter Statement.RETURN_GENERATED_KEYS pour demander les clés générées
        PreparedStatement pre = con.prepareStatement(
                "INSERT INTO Collections.PiecesMonnaie (valeurPiecesMonnaie, unitéPiecesMonnaie) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        pre.setString(1, pieceMonnaie.getValeurPiecesMonnaie());
        pre.setString(2, pieceMonnaie.getUnitéPiecesMonnaie());

        int res = pre.executeUpdate();
        if (res > 0) {
            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = pre.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1); // Premier champ = l'ID généré
                pieceMonnaie.setIdPiecesMonnaie(generatedId); // Mettre à jour l'objet Livre avec l'ID généré
                System.out.println("Piece monnaie ajouté avec ID : " + generatedId);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean supprimerPieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        String req = "DELETE FROM Collections.PiecesMonnaie WHERE idPiecesMonnaie = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, pieceMonnaie.getIdPiecesMonnaie());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean updatePieceMonnaie(PieceMonnaie pieceMonnaie) throws SQLException {
        String req = "UPDATE Collections.PiecesMonnaie SET valeurPiecesMonnaie = ?, unitéPiecesMonnaie = ? WHERE idPiecesMonnaie = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, pieceMonnaie.getValeurPiecesMonnaie());
        pre.setString(2, pieceMonnaie.getUnitéPiecesMonnaie());
        pre.setInt(3, pieceMonnaie.getIdPiecesMonnaie());

        int res = pre.executeUpdate();
        return res > 0;
    }

    @Override
    public PieceMonnaie findById(int id) throws SQLException {
        String req = "SELECT * FROM Collections.PiecesMonnaie WHERE idPiecesMonnaie = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);

        ResultSet resultSet = pre.executeQuery();
        if (resultSet.next()) {
            int idPiecesMonnaie = resultSet.getInt("idPiecesMonnaie");
            String valeurPiecesMonnaie = resultSet.getString("valeurPiecesMonnaie");
            String unitéPiecesMonnaie = resultSet.getString("unitéPiecesMonnaie");

            return new PieceMonnaie(idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie);
        }
        return null;
    }

    @Override
    public List<PieceMonnaie> readALL() throws SQLException {
        List<PieceMonnaie> list = new ArrayList<>();
        String req = "SELECT * FROM Collections.PiecesMonnaie";

        ResultSet resultSet = ste.executeQuery(req);
        while (resultSet.next()) {
            int idPiecesMonnaie = resultSet.getInt("idPiecesMonnaie");
            String valeurPiecesMonnaie = resultSet.getString("valeurPiecesMonnaie");
            String unitéPiecesMonnaie = resultSet.getString("unitéPiecesMonnaie");

            PieceMonnaie pieceMonnaie = new PieceMonnaie(idPiecesMonnaie, valeurPiecesMonnaie, unitéPiecesMonnaie);
            list.add(pieceMonnaie);
        }
        return list;
    }
}
