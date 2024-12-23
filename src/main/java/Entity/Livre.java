package Entity;

public class Livre {
    private int idLivre;
    private String titreLivre;
    private String auteurLivre;
    private int quantite;          // Quantité disponible

    public Livre(int idLivre, String titreLivre, String auteurLivre) {
        this.idLivre = idLivre;
        this.titreLivre = titreLivre;
        this.auteurLivre = auteurLivre;
    }

    // Constructeur complet
    public Livre(int idLivre, String titreLivre, String auteurLivre, int quantite) {
        this.idLivre = idLivre;
        this.titreLivre = titreLivre;
        this.auteurLivre = auteurLivre;
        this.quantite = quantite;
    }

    public int getIdLivre() {
        return idLivre;
    }
    public void setIdLivre(int id) {
        this.idLivre = id;
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

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Méthode toString pour afficher les informations d'un livre
    @Override
    public String toString() {
        return "Livre [ID=" + idLivre + ", Titre=" + titreLivre + ", Auteur=" + auteurLivre
                + ", Quantité=" + quantite + "]";
    }
}
