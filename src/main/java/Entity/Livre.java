package Entity;

public class Livre {
    private int idLIvre;
    private String titreLivre;
    private String auteurLivre;

    public Livre(int idLIvre, String titreLivre, String auteurLivre) {
        this.idLIvre = idLIvre;
        this.titreLivre = titreLivre;
        this.auteurLivre = auteurLivre;
    }

    public int getIdLivre() {
        return idLIvre;
    }
    public void setIdLivre(int id) {
        this.idLIvre = id;
    }

    public String getTitreLivre() {
        return titreLivre;
    }
    public void setTitreLivre(String titre) {
        this.titreLivre = titre;
    }

    public String getAuteurLivre() {
        return auteurLivre;
    }
    public void setAuteurLivre(String auteur) {
        this.auteurLivre = auteur;
    }

    @Override
    public String toString() {
        return "Livre [ID=" + idLIvre + ", Titre=" + titreLivre + ", Auteur=" + auteurLivre+"]";
    }
}
