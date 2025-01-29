CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    OBJECTIF_TOTAL INT NOT NULL DEFAULT 0
);

INSERT INTO utilisateurs (id) 
VALUES (12);
SELECT * FROM utilisateurs;

CREATE TABLE IF NOT EXISTS Collections.logss (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    collection_id INT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Collections.users(id) ON DELETE CASCADE
);
DROP TABLE Collections.utilisateurs;
Describe TABLE Collections.logss;
ALTER TABLE Collections.logss DROP FOREIGN KEY logs_ibfk_1; 
ALTER TABLE Collections.logss ADD CONSTRAINT logs_ibfk_1 FOREIGN KEY (id) REFERENCES Collections.utilisateurs(id) ON DELETE CASCADE;

select * from Collections.typesExistants;
USE Collections;
SHOW TABLES;

select * from Collections.Magasine;


select * from users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    date_naissance VARCHAR(20),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    profession VARCHAR(255),
    nationalite VARCHAR(255),
    langues TEXT
);