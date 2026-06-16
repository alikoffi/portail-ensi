-- =====================================================================
-- Donnees de demonstration : appels de cotisation + paiements (profil dev)
-- =====================================================================

INSERT INTO appel_cotisation (libelle, montant_attendu, date_butoir, date_creation, cloture, version)
SELECT libelle, montant_attendu, date_butoir, date_creation, FALSE, 0
FROM (VALUES
  ('Cotisation mensuelle (echeance passee)', 5000,  CURRENT_DATE - 5,  CURRENT_DATE - 35),
  ('Cotisation trimestrielle',               15000, CURRENT_DATE + 20, CURRENT_DATE - 5)
) AS v(libelle, montant_attendu, date_butoir, date_creation)
WHERE NOT EXISTS (SELECT 1 FROM appel_cotisation);

-- Paiements :
--  - mensuelle (echeance passee) : Koffi & Bamba a jour, Traore partiel (2000/5000),
--    Yao non paye  -> Traore et Yao en retard ; Kone inactif.
--  - trimestrielle (a venir) : Koffi a paye (pas de retard car non echue).
INSERT INTO paiement_cotisation (appel_id, membre_id, montant, date_paiement, note, version)
SELECT (SELECT id FROM appel_cotisation WHERE libelle = v.appel),
       (SELECT id FROM membre WHERE matricule = v.matricule),
       v.montant, v.date_paiement, v.note, 0
FROM (VALUES
  ('Cotisation mensuelle (echeance passee)', 'ENSI-001', 5000,  CURRENT_DATE - 20, NULL),
  ('Cotisation mensuelle (echeance passee)', 'ENSI-002', 5000,  CURRENT_DATE - 18, NULL),
  ('Cotisation mensuelle (echeance passee)', 'ENSI-003', 2000,  CURRENT_DATE - 15, 'Acompte'),
  ('Cotisation trimestrielle',               'ENSI-001', 15000, CURRENT_DATE - 2,  NULL)
) AS v(appel, matricule, montant, date_paiement, note)
WHERE NOT EXISTS (SELECT 1 FROM paiement_cotisation);
