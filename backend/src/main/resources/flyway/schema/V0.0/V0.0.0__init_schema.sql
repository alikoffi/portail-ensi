-- =====================================================================
-- Portail ENSI - Schema initial
-- 6 modules : utilisateurs, evenements, transactions, bilan, pv
-- =====================================================================

-- ---------- UTILISATEURS ----------
CREATE SEQUENCE utilisateur_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE utilisateur (
    id            BIGINT       NOT NULL DEFAULT nextval('utilisateur_id_seq') PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    label         VARCHAR(150),
    actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    create_by     VARCHAR(100),
    create_at     TIMESTAMP,
    update_by     VARCHAR(100),
    update_at     TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0
);

-- ---------- EVENEMENTS ----------
CREATE SEQUENCE evenement_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE evenement (
    id          BIGINT       NOT NULL DEFAULT nextval('evenement_id_seq') PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL,
    date_event  DATE         NOT NULL,
    heure       VARCHAR(20),
    type        VARCHAR(80),
    lieu        VARCHAR(200),
    description TEXT,
    create_by   VARCHAR(100),
    create_at   TIMESTAMP,
    update_by   VARCHAR(100),
    update_at   TIMESTAMP,
    version     BIGINT       NOT NULL DEFAULT 0
);

-- ---------- TRANSACTIONS ----------
CREATE SEQUENCE transaction_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE transaction (
    id        BIGINT         NOT NULL DEFAULT nextval('transaction_id_seq') PRIMARY KEY,
    libelle   VARCHAR(200)   NOT NULL,
    type      VARCHAR(20)    NOT NULL,            -- RECETTE / DEPENSE
    montant   NUMERIC(15, 2) NOT NULL,
    date_tx   DATE           NOT NULL,
    categorie VARCHAR(100),
    note      TEXT,
    create_by VARCHAR(100),
    create_at TIMESTAMP,
    update_by VARCHAR(100),
    update_at TIMESTAMP,
    version   BIGINT         NOT NULL DEFAULT 0
);

-- ---------- BILAN ACTIF ----------
CREATE SEQUENCE bilan_actif_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bilan_actif (
    id        BIGINT         NOT NULL DEFAULT nextval('bilan_actif_id_seq') PRIMARY KEY,
    rubrique  VARCHAR(200)   NOT NULL,
    montant   NUMERIC(15, 2) NOT NULL,
    create_by VARCHAR(100),
    create_at TIMESTAMP,
    update_by VARCHAR(100),
    update_at TIMESTAMP,
    version   BIGINT         NOT NULL DEFAULT 0
);

-- ---------- BILAN PASSIF ----------
CREATE SEQUENCE bilan_passif_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bilan_passif (
    id        BIGINT         NOT NULL DEFAULT nextval('bilan_passif_id_seq') PRIMARY KEY,
    rubrique  VARCHAR(200)   NOT NULL,
    montant   NUMERIC(15, 2) NOT NULL,
    create_by VARCHAR(100),
    create_at TIMESTAMP,
    update_by VARCHAR(100),
    update_at TIMESTAMP,
    version   BIGINT         NOT NULL DEFAULT 0
);

-- ---------- PROCES-VERBAUX ----------
CREATE SEQUENCE pv_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE pv (
    id            BIGINT       NOT NULL DEFAULT nextval('pv_id_seq') PRIMARY KEY,
    objet         VARCHAR(250) NOT NULL,
    date_pv       DATE         NOT NULL,
    lieu          VARCHAR(200),
    presents      TEXT,
    ordre_du_jour TEXT,
    decisions     TEXT,
    signataires   TEXT,
    create_by     VARCHAR(100),
    create_at     TIMESTAMP,
    update_by     VARCHAR(100),
    update_at     TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0
);
