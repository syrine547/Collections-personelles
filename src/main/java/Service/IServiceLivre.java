package Service;
import java.sql.SQLException;
import java.util.List;

public interface IServiceLivre<T> {

    boolean ajouterLivre(T t) throws SQLException;

    boolean supprimerLivre(T t) throws SQLException;

    boolean updateLivre(T t) throws SQLException;

    T findById(int id) throws SQLException;

    List<T> readALL() throws SQLException;
    }
