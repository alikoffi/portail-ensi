-- =====================================================================
-- Documents joints (proces-verbaux / evenements), lien evenement <-> PV
-- et statut d'avancement d'un evenement.
-- =====================================================================

-- ---------- STATUT DE L'EVENEMENT ----------
-- PLANIFIE (defaut) / TERMINE / ANNULE / REPORTE
ALTER TABLE evenement ADD COLUMN statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE';

-- Les evenements deja passes sont consideres comme termines
UPDATE evenement SET statut = 'TERMINE' WHERE date_event < CURRENT_DATE;

-- ---------- LIEN EVENEMENT <-> PV (un PV au plus par evenement) ----------
-- La cle etrangere est portee par le PV : il est cree apres l'evenement,
-- et un PV de bureau sans evenement reste valide (evenement_id NULL).
ALTER TABLE pv ADD COLUMN evenement_id BIGINT REFERENCES evenement (id) ON DELETE SET NULL;

CREATE UNIQUE INDEX uq_pv_evenement ON pv (evenement_id) WHERE evenement_id IS NOT NULL;

-- ---------- DOCUMENTS ----------
CREATE SEQUENCE document_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE document (
    id           BIGINT       NOT NULL DEFAULT nextval('document_id_seq') PRIMARY KEY,
    pv_id        BIGINT       REFERENCES pv (id) ON DELETE CASCADE,
    evenement_id BIGINT       REFERENCES evenement (id) ON DELETE CASCADE,
    libelle      VARCHAR(200),
    nom_original VARCHAR(255) NOT NULL,
    content_type VARCHAR(150) NOT NULL,
    taille       BIGINT       NOT NULL,
    -- LOCAL (disque) ou B2 (Backblaze) : trace le backend d'origine du fichier
    storage_type VARCHAR(20)  NOT NULL,
    dossier      VARCHAR(80)  NOT NULL,
    nom_stocke   VARCHAR(255) NOT NULL,
    -- vrai pour "le" document principal (le PV scanne et signe)
    principal    BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by    VARCHAR(100),
    create_at    TIMESTAMP,
    update_by    VARCHAR(100),
    update_at    TIMESTAMP,
    version      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT ck_document_rattachement CHECK (pv_id IS NOT NULL OR evenement_id IS NOT NULL)
);

CREATE INDEX idx_document_pv ON document (pv_id);
CREATE INDEX idx_document_evenement ON document (evenement_id);
