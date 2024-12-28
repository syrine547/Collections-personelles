package Entity;
import java.util.List;

public class ElementDynamique {
    private int id;
    private List<Object> valeurs; // Liste des valeurs pour chaque colonne

    public ElementDynamique(int id, List<Object> valeurs) {
        this.id = id;
        this.valeurs = valeurs;
    }

    public int getId() {
        return id;
    }

    public List<Object> getValeurs() {
        return valeurs;
    }
}

