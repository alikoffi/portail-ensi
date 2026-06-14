-- =====================================================================
-- Donnees de demonstration (profil dev uniquement)
-- Migration repetable et idempotente : ne duplique jamais les donnees.
-- Mots de passe par defaut a changer imperativement en production.
-- =====================================================================

-- ---------- Comptes par defaut ----------
-- admin / admin123  |  visiteur1 & visiteur2 / visiteur123  (hash bcrypt)
INSERT INTO utilisateur (username, password_hash, role, label, email, actif, version)
VALUES
  ('admin',      '$2a$10$fHCvMQtT9OYDDSkxsoUu1.7QN4LZn/ifl8UKd8W9TP8YOqLgs66Iy', 'ADMIN',      'Administrateur', 'admin@ensi.ci',      TRUE, 0),
  ('tresorier',  '$2a$10$ulaT6jIpSXRmq176MBC43uZT68KOcfeu31OtKfbM4pvDA3fNVs6fq', 'TRESORIER',  'Trésorier',      'tresorier@ensi.ci',  TRUE, 0),
  ('secretaire', '$2a$10$y0ECP4tRSvIUEkw9hsLDH.2Gw/N9TbQi53SfIomFJ6MxXIHS0hqgG', 'SECRETAIRE', 'Secrétaire',     'secretaire@ensi.ci', TRUE, 0),
  ('visiteur1',  '$2a$10$2wYEpa20wCV7cdIjBdMNn.PZtH7ZSNkt/2sJUDv.JOeOAHctaEZU6', 'VIEWER',     'Visiteur 1',     NULL,                 TRUE, 0),
  ('visiteur2',  '$2a$10$2wYEpa20wCV7cdIjBdMNn.PZtH7ZSNkt/2sJUDv.JOeOAHctaEZU6', 'VIEWER',     'Visiteur 2',     NULL,                 TRUE, 0)
ON CONFLICT (username) DO NOTHING;

-- ---------- Evenements de demonstration ----------
INSERT INTO evenement (nom, date_event, heure, type, lieu, description, version)
SELECT nom, date_event, heure, type, lieu, description, 0
FROM (VALUES
  ('Assemblee generale de promotion', CURRENT_DATE + 5,  '18h00', 'Reunion',   'Amphi A - INPHB',     'Bilan du semestre et perspectives.'),
  ('Sortie de terrain - Topographie', CURRENT_DATE + 12, '07h30', 'Sortie',    'Yamoussoukro',        'Releve topographique encadre.'),
  ('Soiree d''integration ENSI',      CURRENT_DATE + 20, '20h00', 'Evenement', 'Foyer des etudiants', 'Accueil des nouveaux membres.'),
  ('Reunion du bureau executif',      CURRENT_DATE - 3,  '17h00', 'Reunion',   'Salle de reunion',    'Preparation du budget.')
) AS v(nom, date_event, heure, type, lieu, description)
WHERE NOT EXISTS (SELECT 1 FROM evenement);

-- ---------- Transactions de demonstration ----------
INSERT INTO transaction (libelle, type, montant, date_tx, categorie, note, version)
SELECT libelle, type, montant, date_tx, categorie, note, 0
FROM (VALUES
  ('Cotisations promotion - Janvier', 'RECETTE', 250000, CURRENT_DATE - 30, 'Cotisations',   'Versement de 50 membres'),
  ('Subvention direction',            'RECETTE', 150000, CURRENT_DATE - 18, 'Subvention',    NULL),
  ('Achat materiel evenement',        'DEPENSE',  75000, CURRENT_DATE - 10, 'Logistique',    'Sono et decoration'),
  ('Impression supports',             'DEPENSE',  30000, CURRENT_DATE - 4,  'Communication', NULL),
  ('Cotisations promotion - Fevrier', 'RECETTE', 120000, CURRENT_DATE - 2,  'Cotisations',   NULL)
) AS v(libelle, type, montant, date_tx, categorie, note)
WHERE NOT EXISTS (SELECT 1 FROM transaction);

-- ---------- Bilan actif ----------
INSERT INTO bilan_actif (rubrique, montant, version)
SELECT rubrique, montant, 0
FROM (VALUES
  ('Tresorerie (caisse + banque)', 415000),
  ('Materiel et equipements',      120000),
  ('Creances (cotisations dues)',   65000)
) AS v(rubrique, montant)
WHERE NOT EXISTS (SELECT 1 FROM bilan_actif);

-- ---------- Bilan passif ----------
INSERT INTO bilan_passif (rubrique, montant, version)
SELECT rubrique, montant, 0
FROM (VALUES
  ('Fonds associatif',        500000),
  ('Dettes fournisseurs',      60000),
  ('Provisions evenements',    40000)
) AS v(rubrique, montant)
WHERE NOT EXISTS (SELECT 1 FROM bilan_passif);

-- ---------- Proces-verbaux ----------
INSERT INTO pv (objet, date_pv, lieu, presents, ordre_du_jour, decisions, signataires, version)
SELECT objet, date_pv, lieu, presents, ordre_du_jour, decisions, signataires, 0
FROM (VALUES
  (
    'Reunion de constitution du bureau',
    CURRENT_DATE - 45,
    'Salle de reunion - INPHB',
    'President, Vice-president, Tresorier, Secretaire, 12 membres',
    E'1. Election du bureau\n2. Definition des cotisations\n3. Calendrier previsionnel',
    E'- Bureau elu a l''unanimite\n- Cotisation fixee a 5000 FCFA / membre / mois\n- Premiere assemblee planifiee',
    'Le President, Le Secretaire'
  ),
  (
    'Reunion budgetaire trimestrielle',
    CURRENT_DATE - 10,
    'Amphi A - INPHB',
    'President, Tresorier, Secretaire, 20 membres',
    E'1. Point de tresorerie\n2. Validation des depenses\n3. Preparation de la soiree d''integration',
    E'- Solde valide a 415 000 FCFA\n- Budget integration approuve (75 000 FCFA)\n- Commission logistique designee',
    'Le President, Le Tresorier, Le Secretaire'
  )
) AS v(objet, date_pv, lieu, presents, ordre_du_jour, decisions, signataires)
WHERE NOT EXISTS (SELECT 1 FROM pv);

-- ---------- Membres ----------
INSERT INTO membre (nom, prenoms, matricule, email, telephone, specialite, statut, date_adhesion, version)
SELECT nom, prenoms, matricule, email, telephone, specialite, statut, date_adhesion, 0
FROM (VALUES
  ('Koffi',   'Anselme',  'ENSI-001', 'anselme.koffi@inphb.ci',  '0700000001', 'Topographie',  'ACTIF',   CURRENT_DATE - 60),
  ('Bamba',   'Awa',      'ENSI-002', 'awa.bamba@inphb.ci',      '0700000002', 'SIG',          'ACTIF',   CURRENT_DATE - 58),
  ('Traore',  'Ismael',   'ENSI-003', 'ismael.traore@inphb.ci',  '0700000003', 'Cartographie', 'ACTIF',   CURRENT_DATE - 55),
  ('Kone',    'Mariam',   'ENSI-004', 'mariam.kone@inphb.ci',    '0700000004', 'BTP',          'INACTIF', CURRENT_DATE - 50),
  ('Yao',     'Patrick',  'ENSI-005', 'patrick.yao@inphb.ci',    '0700000005', 'Topographie',  'ACTIF',   CURRENT_DATE - 45)
) AS v(nom, prenoms, matricule, email, telephone, specialite, statut, date_adhesion)
WHERE NOT EXISTS (SELECT 1 FROM membre);

-- ---------- Cotisations ----------
-- Periodes au format AAAA-MM, relatives au mois courant :
--   M2 = il y a 2 mois (mois d'adhesion), M1 = mois precedent (dernier mois echu).
-- Koffi & Traore a jour (M2 + M1) ; Bamba en retard d'1 mois (M2 seul) ;
-- Yao en retard de 2 mois (aucun paiement) ; Kone inactif (ignore).
INSERT INTO cotisation (membre_id, periode, montant, date_paiement, note, version)
SELECT (SELECT id FROM membre WHERE matricule = v.matricule), v.periode, v.montant, v.date_paiement, v.note, 0
FROM (VALUES
  ('ENSI-001', to_char(CURRENT_DATE - INTERVAL '2 month', 'YYYY-MM'), 5000, CURRENT_DATE - 40, NULL),
  ('ENSI-001', to_char(CURRENT_DATE - INTERVAL '1 month', 'YYYY-MM'), 5000, CURRENT_DATE - 10, NULL),
  ('ENSI-002', to_char(CURRENT_DATE - INTERVAL '2 month', 'YYYY-MM'), 5000, CURRENT_DATE - 38, NULL),
  ('ENSI-003', to_char(CURRENT_DATE - INTERVAL '2 month', 'YYYY-MM'), 5000, CURRENT_DATE - 35, 'Paiement en espèces'),
  ('ENSI-003', to_char(CURRENT_DATE - INTERVAL '1 month', 'YYYY-MM'), 5000, CURRENT_DATE - 8,  NULL)
) AS v(matricule, periode, montant, date_paiement, note)
WHERE NOT EXISTS (SELECT 1 FROM cotisation);
