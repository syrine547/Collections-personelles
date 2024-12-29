CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE
);
ALTER TABLE Collections.typesExistants ADD description TEXT;
ALTER TABLE Collections.typesExistants ADD dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Collections.typesExistants ADD COLUMN OBJECTIF_TOTAL INT NOT NULL DEFAULT 0;
ALTER TABLE Collections.typesExistants ADD COLUMN fxmlFile VARCHAR(255);


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
idCartePostale int primary key not null AUTO_INCREMENT,
titreCartePostale varchar(50)
);

CREATE TABLE Collections.PiecesMonnaie (
idPiecesMonnaie INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
valeurPiecesMonnaie VARCHAR(50),
unitéPiecesMonnaie VARCHAR(50)
);

ALTER TABLE Collections.Livres ADD quantité INT DEFAULT 1;
ALTER TABLE Collections.Timbres ADD quantité INT DEFAULT 1;
ALTER TABLE Collections.PiecesMonnaie ADD quantité INT DEFAULT 1;
ALTER TABLE Collections.CartePostale ADD quantité INT DEFAULT 1;

/*TRUNCATE TABLE Collections.Livres;*/

select * from Collections.typesExistants;
USE Collections;
SHOW TABLES;

select * from Collections.Livres;
select * from Collections.Timbres;
select * from Collections.CartePostale;
select * from Collections.PiecesMonnaie;


describe Collections.collectionx;