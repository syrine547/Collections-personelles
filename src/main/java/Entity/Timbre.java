package Entity;

public class Timbre {
    private int idTimbre;
    private String nomTimbre;
    private int quantite;

    // Constructeurs
    public Timbre(int idTimbre, String nomTimbre, int quantite) {
        this.idTimbre = idTimbre;
        this.nomTimbre = nomTimbre;
        this.quantite = quantite;
    }

    public Timbre(String nomTimbre, int quantite) {
        this.nomTimbre = nomTimbre;
        this.quantite = quantite;
    }

    public Timbre(String nomTimbre) {
        this.nomTimbre = nomTimbre;
        this.quantite = 1; // Quantité par défaut
    }

    // Getters et Setters
    public int getIdTimbre() {
        return idTimbre;
    }

    public void setIdTimbre(int idTimbre) {
        this.idTimbre = idTimbre;
    }

    public String getNomTimbre() {
        return nomTimbre;
    }

    public void setNomTimbre(String nomTimbre) {
        this.nomTimbre = nomTimbre;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Méthode toString pour l'affichage
    @Override
    public String toString() {
        return "Timbre [ID=" + idTimbre + ", Nom=" + nomTimbre + ", Quantité=" + quantite + "]";
    }
}
