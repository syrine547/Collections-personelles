CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    OBJECTIF_TOTAL INT NOT NULL DEFAULT 0
);
UPDATE Collections.typesExistants
SET user_id = 1  -- Remplacez par une valeur valide
WHERE user_id IS NULL;



ALTER TABLE Collections.typesExistants 
modify COLUMN user_id INT not null,
ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES Collections.users(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS Collections.logss (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    collection_id INT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Collections.users(id) ON DELETE CASCADE
);

CREATE TABLE Collections.users (
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

ALTER TABLE Collections.users 
ADD COLUMN role ENUM('admin', 'user') DEFAULT 'user';

describe Collections.typesExistants;
select * from Collections.typesExistants;
select * from Collections.users;

USE Collections;
SHOW TABLES;
