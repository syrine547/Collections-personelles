CREATE DATABASE Collections;

CREATE TABLE Collections.typesExistants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomType VARCHAR(255) NOT NULL UNIQUE
);

UPDATE Collections.typesExistants
SET fxmlFile = 'gestionCollection.fxml'
WHERE nomType = 'Magasine';


select * from Collections.typesExistants;
USE Collections;
SHOW TABLES;

INSERT INTO Collections.Magasine (titre, auteur) VALUES ('Majed','Al-yamama');

select * from Collections.Magasine;
describe Collections.Magasine;