-- =====================================================================
-- Refonte des cotisations : appels de cotisation + paiements par membre
-- Remplace l'ancien modele "cotisation" (periode mensuelle).
-- =====================================================================

-- Ancien modele
DROP TABLE IF EXISTS cotisation;
DROP SEQUENCE IF EXISTS cotisation_id_seq;

-- ---------- APPEL DE COTISATION ----------
CREATE SEQUENCE appel_cotisation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE appel_cotisation (
    id              BIGINT         NOT NULL DEFAULT nextval('appel_cotisation_id_seq') PRIMARY KEY,
    libelle         VARCHAR(200)   NOT NULL,
    montant_attendu NUMERIC(15, 2) NOT NULL,
    date_butoir     DATE           NOT NULL,
    date_creation   DATE           NOT NULL DEFAULT CURRENT_DATE,
    cloture         BOOLEAN        NOT NULL DEFAULT FALSE,
    create_by       VARCHAR(100),
    create_at       TIMESTAMP,
    update_by       VARCHAR(100),
    update_at       TIMESTAMP,
    version         BIGINT         NOT NULL DEFAULT 0
);

-- ---------- PAIEMENT (montant verse par un membre pour un appel) ----------
CREATE SEQUENCE paiement_cotisation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE paiement_cotisation (
    id            BIGINT         NOT NULL DEFAULT nextval('paiement_cotisation_id_seq') PRIMARY KEY,
    appel_id      BIGINT         NOT NULL REFERENCES appel_cotisation (id) ON DELETE CASCADE,
    membre_id     BIGINT         NOT NULL REFERENCES membre (id) ON DELETE CASCADE,
    montant       NUMERIC(15, 2) NOT NULL,
    date_paiement DATE           NOT NULL,
    note          TEXT,
    create_by     VARCHAR(100),
    create_at     TIMESTAMP,
    update_by     VARCHAR(100),
    update_at     TIMESTAMP,
    version       BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uq_paiement_appel_membre UNIQUE (appel_id, membre_id)
);

CREATE INDEX idx_paiement_appel ON paiement_cotisation (appel_id);
CREATE INDEX idx_paiement_membre ON paiement_cotisation (membre_id);
