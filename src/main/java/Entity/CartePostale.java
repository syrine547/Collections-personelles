package Entity;

public class CartePostale {
    private int idCartePostale;
    private String titreCartePostale;

    public CartePostale(int idCartePostale, String titreCartePostale) {
        this.idCartePostale = idCartePostale;
        this.titreCartePostale = titreCartePostale;
    }

    public int getIdCartePostale() {
        return idCartePostale;
    }
    public void setIdCartePostale(int id) {
        this.idCartePostale = id;
    }

    public String getTitreCartePostale() {
        return titreCartePostale;
    }
    public void setTitreCartePostale(String titre) {
        this.titreCartePostale = titre;
    }

    @Override
    public String toString() {
        return "Carte postale [ID=" + idCartePostale + ", Titre=" + titreCartePostale + "]";
    }
}
