CREATE DATABASE Collections;

CREATE TABLE Collections.Livres (
idLIvre INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
titreLivre VARCHAR(50),
auteurLivre VARCHAR(50)
);

CREATE TABLE Collections.Timbres (
idTimbre int PRIMARY KEY NOT NULL AUTO_INCREMENT,
nomTimbre VARCHAR(50)
);

CREATE table Collections.CartePostale (
idCartePostale int primary key AUTO_INCREMENT,
titreCartePostale varchar(50)
);

CREATE TABLE Collections.PiecesMonnaie (
idPiecesMonnaie INT PRIMARY KEY NOT NULL,
valeurPiecesMonnaie VARCHAR(50),
unitéPiecesMonnaie VARCHAR(50)
);

ALTER TABLE Collections.Livres ADD nbLivresEstimee DOUBLE DEFAULT 0.0;
ALTER TABLE Collections.Timbres ADD nbTimbresEstimee DOUBLE DEFAULT 0.0;
ALTER TABLE Collections.PiecesMonnaie ADD nbPiecesMonnaieEstimee DOUBLE DEFAULT 0.0;
ALTER TABLE Collections.CartePostale ADD nbCartesPostaleEstimee DOUBLE DEFAULT 0.0;

TRUNCATE TABLE Collections.Livres;

select * from Collections.Livres;
select * from Collections.Timbres;
select * from Collections.CartePostale;
select * from Collections.PiecesMonnaie;