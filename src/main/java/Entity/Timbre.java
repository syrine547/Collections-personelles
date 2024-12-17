package Entity;

public class Timbre {
    private int idTimbre;
    private String nomTimbre;

    public Timbre(int idTimbre, String nomTimbre) {
        this.idTimbre = idTimbre;
        this.nomTimbre = nomTimbre;
    }
    public Timbre(String nomTimbre) {
        this.nomTimbre = nomTimbre;
    }
    public int getIdTimbre() {
        return idTimbre;
    }
    public void setIdTimbre(int id) {
        this.idTimbre = id;
    }

    public String getNomTimbre() {
        return nomTimbre;
    }
    public void setNomTimbre(String nomTimbre) {
        this.nomTimbre = nomTimbre;
    }

    @Override
    public String toString() {
        return "Timbre [ID=" + idTimbre + ", nom =" + nomTimbre + "]";
    }
}
