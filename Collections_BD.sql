CREATE DATABASE Collections;

CREATE TABLE Collections.Livres (
idLIvre INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
titreLivre VARCHAR(50),
auteurLivre VARCHAR(50)
);

CREATE TABLE Collections.Timbres (
idTimbre int PRIMARY KEY NOT NULL,
nomTimbre VARCHAR(50)
);

CREATE table Collections.CartePostale (
idCartePostale int primary key,
titreCartePostale varchar(50)
);

CREATE TABLE Collections.PiecesMonnaie (
idPiecesMonnaie INT PRIMARY KEY NOT NULL,
valeurPiecesMonnaie VARCHAR(50),
unitéPiecesMonnaie VARCHAR(50)
);

TRUNCATE TABLE Collections.Livres;

select * from Collections.Livres;
select * from Collections.Timbres;
select * from Collections.CartePostale;
select * from Collections.PiecesMonnaie;