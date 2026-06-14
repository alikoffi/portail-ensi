-- =====================================================================
-- Portail ENSI - Membres et cotisations
-- =====================================================================

-- ---------- MEMBRES ----------
CREATE SEQUENCE membre_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE membre (
    id            BIGINT       NOT NULL DEFAULT nextval('membre_id_seq') PRIMARY KEY,
    nom           VARCHAR(120) NOT NULL,
    prenoms       VARCHAR(150),
    matricule     VARCHAR(60),
    email         VARCHAR(150),
    telephone     VARCHAR(40),
    specialite    VARCHAR(120),
    statut        VARCHAR(20)  NOT NULL DEFAULT 'ACTIF',
    date_adhesion DATE,
    create_by     VARCHAR(100),
    create_at     TIMESTAMP,
    update_by     VARCHAR(100),
    update_at     TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0
);

-- ---------- COTISATIONS ----------
CREATE SEQUENCE cotisation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE cotisation (
    id            BIGINT         NOT NULL DEFAULT nextval('cotisation_id_seq') PRIMARY KEY,
    membre_id     BIGINT         NOT NULL REFERENCES membre (id) ON DELETE CASCADE,
    periode       VARCHAR(60)    NOT NULL,
    montant       NUMERIC(15, 2) NOT NULL,
    date_paiement DATE           NOT NULL,
    note          TEXT,
    create_by     VARCHAR(100),
    create_at     TIMESTAMP,
    update_by     VARCHAR(100),
    update_at     TIMESTAMP,
    version       BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_cotisation_membre ON cotisation (membre_id);
