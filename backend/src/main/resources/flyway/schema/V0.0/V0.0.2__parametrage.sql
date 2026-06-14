-- =====================================================================
-- Portail ENSI - Parametrage (valeurs de reference)
-- =====================================================================

CREATE SEQUENCE parametrage_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE parametrage (
    id        BIGINT       NOT NULL DEFAULT nextval('parametrage_id_seq') PRIMARY KEY,
    categorie VARCHAR(60)  NOT NULL,
    libelle   VARCHAR(150) NOT NULL,
    ordre     INT          NOT NULL DEFAULT 0,
    actif     BOOLEAN      NOT NULL DEFAULT TRUE,
    create_by VARCHAR(100),
    create_at TIMESTAMP,
    update_by VARCHAR(100),
    update_at TIMESTAMP,
    version   BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uq_parametrage UNIQUE (categorie, libelle)
);

CREATE INDEX idx_parametrage_categorie ON parametrage (categorie);

-- Valeurs usuelles
INSERT INTO parametrage (categorie, libelle, ordre) VALUES
  ('TYPE_EVENEMENT', 'Réunion',       1),
  ('TYPE_EVENEMENT', 'Assemblée',     2),
  ('TYPE_EVENEMENT', 'Sortie',        3),
  ('TYPE_EVENEMENT', 'Évènement',     4),
  ('TYPE_EVENEMENT', 'Sport',         5),
  ('TYPE_EVENEMENT', 'Formation',     6),
  ('TYPE_EVENEMENT', 'Cérémonie',     7),

  ('CATEGORIE_TRANSACTION', 'Cotisations',    1),
  ('CATEGORIE_TRANSACTION', 'Subvention',     2),
  ('CATEGORIE_TRANSACTION', 'Don',            3),
  ('CATEGORIE_TRANSACTION', 'Logistique',     4),
  ('CATEGORIE_TRANSACTION', 'Communication',  5),
  ('CATEGORIE_TRANSACTION', 'Restauration',   6),
  ('CATEGORIE_TRANSACTION', 'Transport',      7),
  ('CATEGORIE_TRANSACTION', 'Divers',         8),

  ('SPECIALITE', 'Topographie',  1),
  ('SPECIALITE', 'SIG',          2),
  ('SPECIALITE', 'Cartographie', 3),
  ('SPECIALITE', 'BTP',          4),
  ('SPECIALITE', 'Géomatique',   5),
  ('SPECIALITE', 'Génie civil',  6);
