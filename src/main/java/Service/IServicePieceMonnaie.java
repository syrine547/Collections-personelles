package Service;

import java.sql.SQLException;
import java.util.List;

public interface IServicePieceMonnaie<T> {

    boolean ajouterPieceMonnaie(T t) throws SQLException;

    boolean supprimerPieceMonnaie(T t) throws SQLException;

    boolean updatePieceMonnaie(T t) throws SQLException;

    T findById(int id) throws SQLException;

    List<T> readALL() throws SQLException;
}
