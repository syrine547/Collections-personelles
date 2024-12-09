package org.example;

import Service.ServiceLivre;
import Entity.Livre;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceLivre serviceLivre = new ServiceLivre();

        try {
            // Ajouter un livre
            Livre livre1 = new Livre(0, "Le Petit Prince", "Antoine de Saint-Exupéry");
            Livre livre2 = new Livre(0, "1984", "George Orwell");

            System.out.println("Ajout de livres...");
            serviceLivre.ajouterLivre(livre1);
            serviceLivre.ajouterLivre(livre2);

            // Lire tous les livres
            System.out.println("Liste des livres :");
            List<Livre> livres = serviceLivre.readALL();
            for (Livre livre : livres) {
                System.out.println(livre);
            }

            // Modifier un livre
            System.out.println("Mise à jour du titre du livre avec ID 1...");
            livre1.setTitreLivre("Le Petit Prince - Édition spéciale");
            serviceLivre.updateLivre(livre1);

            // Trouver un livre par ID
            System.out.println("Recherche du livre avec ID 1...");
            Livre foundLivre = serviceLivre.findById(1);
            if (foundLivre != null) {
                System.out.println("Livre trouvé : " + foundLivre);
            } else {
                System.out.println("Livre non trouvé.");
            }

            // Supprimer un livre
            System.out.println("Suppression du livre avec ID 2...");
            serviceLivre.supprimerLivre(livre2);

            // Lire tous les livres après suppression
            System.out.println("Liste des livres après suppression :");
            livres = serviceLivre.readALL();
            for (Livre livre : livres) {
                System.out.println(livre);
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
