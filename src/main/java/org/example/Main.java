package org.example;

import Entity.*;
import Service.*;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Gestion des livres
            System.out.println("--- Gestion des Livres ---");
            ServiceLivre serviceLivre = new ServiceLivre();

            Livre livre1 = new Livre(0, "Les Misérables", "Victor Hugo");
            Livre livre2 = new Livre(0, "Le Petit Prince", "Antoine de Saint-Exupéry");

            serviceLivre.ajouterLivre(livre1);
            serviceLivre.ajouterLivre(livre2);

            List<Livre> livres = serviceLivre.readALL();
            for (Livre livre : livres) {
                System.out.println(livre);
            }

            // Gestion des pièces de monnaie
            System.out.println("\n--- Gestion des Pièces de Monnaie ---");
            ServicePieceMonnaie servicePieceMonnaie = new ServicePieceMonnaie();

            PieceMonnaie piece1 = new PieceMonnaie(0, "1 Euro", "EUR");
            PieceMonnaie piece2 = new PieceMonnaie(0, "50 Centimes", "EUR");

            servicePieceMonnaie.ajouterPieceMonnaie(piece1);
            servicePieceMonnaie.ajouterPieceMonnaie(piece2);

            List<PieceMonnaie> pieces = servicePieceMonnaie.readALL();
            for (PieceMonnaie piece : pieces) {
                System.out.println(piece);
            }

            // Gestion des cartes postales
            System.out.println("\n--- Gestion des Cartes Postales ---");
            ServiceCartePostale serviceCartePostale = new ServiceCartePostale();

            CartePostale carte1 = new CartePostale(0, "Tour Eiffel");
            CartePostale carte2 = new CartePostale(0, "Mont Saint-Michel");

            serviceCartePostale.ajouterCartePostale(carte1);
            serviceCartePostale.ajouterCartePostale(carte2);

            List<CartePostale> cartes = serviceCartePostale.readALL();
            for (CartePostale carte : cartes) {
                System.out.println(carte);
            }

            // Gestion des timbres
            System.out.println("\n--- Gestion des Timbres ---");
            ServiceTimbre serviceTimbre = new ServiceTimbre();

            // Ajout de nouveaux timbres
            System.out.println("Ajout de timbres...");
            Timbre timbre1 = new Timbre("Timbre Rouge");
            Timbre timbre2 = new Timbre("Timbre Bleu");

            serviceTimbre.ajouterTimbre(timbre1);
            serviceTimbre.ajouterTimbre(timbre2);

            // Lecture et affichage de tous les timbres
            System.out.println("Liste des timbres dans la base de données :");
            List<Timbre> timbres = serviceTimbre.readALL();
            for (Timbre t : timbres) {
                System.out.println(t);
            }
            // --- Tester la méthode findById ---
            System.out.println("\n--- Tester la méthode findById ---");
            int idRecherche = 1; // Remplacez par un ID existant
            Timbre timbreTrouve = serviceTimbre.findById(idRecherche);
            if (timbreTrouve != null) {
                System.out.println("Timbre trouvé : " + timbreTrouve);
            } else {
                System.out.println("Aucun timbre trouvé avec l'ID " + idRecherche);
            }

            // --- Tester la méthode supprimerTimbre ---
            System.out.println("\n--- Tester la méthode supprimerTimbre ---");
            if (timbreTrouve != null) {
                boolean supprime = serviceTimbre.supprimerTimbre(timbreTrouve);
                if (supprime) {
                    System.out.println("Timbre supprimé avec succès : " + timbreTrouve);
                } else {
                    System.out.println("Échec de la suppression du timbre avec l'ID " + idRecherche);
                }
            }

            // Vérifier si le timbre a été supprimé
            System.out.println("\n--- Vérification après suppression ---");
            List<Timbre> timbresApresSuppression = serviceTimbre.readALL();
            System.out.println("Liste des timbres après suppression :");
            for (Timbre t : timbresApresSuppression) {
                System.out.println(t);
            }

            System.out.println("\n--- Statistiques ---\n");

            serviceLivre.afficherRapport("Livres");
            serviceCartePostale.afficherRapport("Cartes Postales");
            serviceTimbre.afficherRapport("Timbres");
            servicePieceMonnaie.afficherRapport("Pieces Monnaies");

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}