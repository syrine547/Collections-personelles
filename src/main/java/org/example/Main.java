package org.example;

import Entity.Livre;
import Entity.PieceMonnaie;
import Entity.CartePostale;
import Service.ServiceLivre;
import Service.ServicePieceMonnaie;
import Service.ServiceCartePostale;

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

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
