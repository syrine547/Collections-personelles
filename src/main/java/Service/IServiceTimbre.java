package Service;

import java.sql.SQLException;
import java.util.List;

public interface IServiceTimbre<T> {

    boolean ajouterTimbre(T t) throws SQLException;

    boolean supprimerTimbre(T t) throws SQLException;

    boolean updateTimbre(T t) throws SQLException;

    T findById(int id) throws SQLException;

    List<T> readALL() throws SQLException;

}
