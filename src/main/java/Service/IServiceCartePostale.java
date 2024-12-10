package Service;

import java.sql.SQLException;
import java.util.List;

public interface IServiceCartePostale<T> {

    boolean ajouterCartePostale(T t) throws SQLException;

    boolean supprimerCartePostale(T t) throws SQLException;

    boolean updateCartePostale(T t) throws SQLException;

    T findById(int id) throws SQLException;

    List<T> readALL() throws SQLException;
}
