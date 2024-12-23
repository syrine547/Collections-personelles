package Entity;

public class CartePostale {
    private int idCartePostale;
    private String titreCartePostale;
    private int quantite;

    public CartePostale(int idCartePostale, String titreCartePostale, int quantite) {
        this.idCartePostale = idCartePostale;
        this.titreCartePostale = titreCartePostale;
        this.quantite = quantite;
    }

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

    public int getQuantite() {return quantite;}
    public void setQuantite(int quantite) {this.quantite = quantite;}

    @Override
    public String toString() {
        return "Carte postale [ID=" + idCartePostale + ", Titre=" + titreCartePostale + "]";
    }
}
