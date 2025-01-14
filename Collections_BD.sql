CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    OBJECTIF_TOTAL INT NOT NULL DEFAULT 0
);

select * from Collections.typesExistants;
USE Collections;
SHOW TABLES;

select * from Collections.Magasine;