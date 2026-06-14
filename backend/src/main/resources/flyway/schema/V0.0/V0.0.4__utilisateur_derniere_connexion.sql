-- Suivi de la derniere connexion des comptes
ALTER TABLE utilisateur ADD COLUMN derniere_connexion TIMESTAMP;
