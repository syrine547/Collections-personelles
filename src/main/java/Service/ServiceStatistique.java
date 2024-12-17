package Service;

import java.sql.SQLException;

public interface ServiceStatistique {
    int getNombreTotal() throws SQLException;
    int getObjectifTotal();

    // Méthode pour calculer le pourcentage atteint
    default double getPourcentageAtteint() throws SQLException {
        return (double) getNombreTotal() / getObjectifTotal() * 100;
    }

    // Méthode pour afficher le rapport
    default void afficherRapport(String nomCategorie) throws SQLException {
        System.out.println("---- Rapport des " + nomCategorie + " ----");
        System.out.println("Objectif fixé : " + getObjectifTotal());
        System.out.println("Nombre actuel : " + getNombreTotal());
        System.out.printf("Pourcentage atteint : %.2f%%\n", getPourcentageAtteint());
    }
}
