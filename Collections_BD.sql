CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    OBJECTIF_TOTAL INT NOT NULL DEFAULT 0
);

CREATE TABLE Collections.users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    date_naissance DATE,
    sexe ENUM('Homme', 'Femme'),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    profession VARCHAR(255),
    nationalite VARCHAR(255),
    langues TEXT
);

select * from Collections.typesExistants;
USE Collections;
SHOW TABLES;

select * from Collections.Magasine;