-- =====================================================================
-- Ecrans de l'application : l'administrateur peut en masquer.
-- Un ecran masque disparait pour tout le monde, quel que soit le role.
-- =====================================================================

CREATE SEQUENCE ecran_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE ecran (
    id           BIGINT       NOT NULL DEFAULT nextval('ecran_id_seq') PRIMARY KEY,
    code         VARCHAR(60)  NOT NULL UNIQUE,
    libelle      VARCHAR(120) NOT NULL,
    -- Prefixes d'API couverts, separes par des virgules. Vide = ecran sans API
    -- propre. Sert a refuser aussi les appels directs, pas seulement le menu.
    prefixes_api VARCHAR(400),
    -- Un ecran verrouille ne peut pas etre masque : sans lui, plus aucun moyen
    -- de revenir en arriere.
    verrouille   BOOLEAN      NOT NULL DEFAULT FALSE,
    visible      BOOLEAN      NOT NULL DEFAULT TRUE,
    ordre        INTEGER      NOT NULL DEFAULT 0,
    create_by    VARCHAR(100),
    create_at    TIMESTAMP,
    update_by    VARCHAR(100),
    update_at    TIMESTAMP,
    version      BIGINT       NOT NULL DEFAULT 0
);

INSERT INTO ecran (code, libelle, prefixes_api, verrouille, visible, ordre, version) VALUES
  ('TABLEAU_DE_BORD', 'Tableau de bord',  '/ws/tableau-de-bord',            TRUE,  TRUE, 10, 0),
  ('PLANNING',        'Planning',         '/ws/evenement',                  FALSE, TRUE, 20, 0),
  ('FINANCES',        'États financiers', '/ws/transaction,/ws/bilan',      FALSE, TRUE, 30, 0),
  ('MEMBRES',         'Membres',          '/ws/membre',                     FALSE, TRUE, 40, 0),
  ('COTISATIONS',     'Cotisations',      '/ws/appel-cotisation',           FALSE, TRUE, 50, 0),
  ('PROCES_VERBAUX',  'Procès-verbaux',   '/ws/pv',                         FALSE, TRUE, 60, 0),
  ('COMPTES',         'Comptes',          '/ws/utilisateur',                TRUE,  TRUE, 70, 0),
  ('PARAMETRAGE',     'Paramétrage',      '/ws/parametrage',                TRUE,  TRUE, 80, 0),
  ('ECRANS',          'Écrans',           '/ws/ecran',                      TRUE,  TRUE, 90, 0);
