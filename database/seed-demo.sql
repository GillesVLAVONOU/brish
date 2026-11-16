BEGIN;

DO $$
BEGIN
    IF to_regclass('public.ingredients') IS NULL
       OR to_regclass('public.fournisseurs') IS NULL
       OR to_regclass('public.achats') IS NULL
       OR to_regclass('public.mets') IS NULL
       OR to_regclass('public.recettes') IS NULL
       OR to_regclass('public.productions') IS NULL THEN
        RAISE EXCEPTION 'Le schema Brish est absent. Initialisez la base avant la seed.';
    END IF;
END
$$;

INSERT INTO ingredients (nom, unite_base, seuil_alerte, actif)
VALUES
    ('Riz local - Démo', 'kg', 5, TRUE),
    ('Poulet fermier - Démo', 'kg', 3, TRUE),
    ('Poisson frais - Démo', 'kg', 2, TRUE),
    ('Tomate - Démo', 'kg', 2, TRUE),
    ('Oignon - Démo', 'kg', 1, TRUE),
    ('Huile végétale - Démo', 'L', 2, TRUE),
    ('Sel - Démo', 'kg', 1, TRUE),
    ('Piment - Démo', 'kg', 0.5, TRUE),
    ('Haricots verts - Démo', 'kg', 2, TRUE),
    ('Farine de blé - Démo', 'kg', 3, TRUE),
    ('Lait - Démo', 'L', 2, TRUE),
    ('Sucre - Démo', 'kg', 2, TRUE)
ON CONFLICT (nom) DO UPDATE
SET unite_base = EXCLUDED.unite_base,
    seuil_alerte = EXCLUDED.seuil_alerte,
    actif = TRUE;

INSERT INTO fournisseurs (nom, telephone, email, adresse)
VALUES
    ('Marché Central - Démo', '+229 00 00 00 01', 'demo.marche@brish.example', 'Cotonou'),
    ('Ferme du Littoral - Démo', '+229 00 00 00 02', 'demo.ferme@brish.example', 'Abomey-Calavi'),
    ('Épicerie du Port - Démo', '+229 00 00 00 03', 'demo.epicerie@brish.example', 'Cotonou')
ON CONFLICT (email) DO UPDATE
SET nom = EXCLUDED.nom,
    telephone = EXCLUDED.telephone,
    adresse = EXCLUDED.adresse;

INSERT INTO achats (id_fournisseur, date_achat, numero_facture)
SELECT f.id_fournisseur, CURRENT_DATE + d.jours, d.facture
FROM (VALUES
    ('demo.marche@brish.example', -21, 'BRISH-DEMO-ACH-001'),
    ('demo.ferme@brish.example', -10, 'BRISH-DEMO-ACH-002'),
    ('demo.epicerie@brish.example', -5, 'BRISH-DEMO-ACH-003')
) AS d(email, jours, facture)
JOIN fournisseurs f ON f.email = d.email
ON CONFLICT (numero_facture) DO UPDATE
SET id_fournisseur = EXCLUDED.id_fournisseur,
    date_achat = EXCLUDED.date_achat;

DELETE FROM lignes_achat la
USING achats a
WHERE la.id_achat = a.id_achat
  AND a.numero_facture IN (
      'BRISH-DEMO-ACH-001', 'BRISH-DEMO-ACH-002', 'BRISH-DEMO-ACH-003'
  );

INSERT INTO lignes_achat (id_achat, id_ingredient, quantite, unite, prix_unitaire)
SELECT a.id_achat, i.id_ingredient, d.quantite, d.unite, d.prix
FROM (VALUES
    ('BRISH-DEMO-ACH-001', 'Riz local - Démo', 20.000, 'kg', 850.00),
    ('BRISH-DEMO-ACH-001', 'Tomate - Démo', 8.000, 'kg', 700.00),
    ('BRISH-DEMO-ACH-001', 'Oignon - Démo', 6.000, 'kg', 550.00),
    ('BRISH-DEMO-ACH-001', 'Poulet fermier - Démo', 12.000, 'kg', 3200.00),
    ('BRISH-DEMO-ACH-002', 'Poisson frais - Démo', 10.000, 'kg', 3800.00),
    ('BRISH-DEMO-ACH-002', 'Huile végétale - Démo', 8.000, 'L', 1400.00),
    ('BRISH-DEMO-ACH-002', 'Sel - Démo', 4.000, 'kg', 300.00),
    ('BRISH-DEMO-ACH-002', 'Piment - Démo', 2.000, 'kg', 2400.00),
    ('BRISH-DEMO-ACH-003', 'Haricots verts - Démo', 10.000, 'kg', 1300.00),
    ('BRISH-DEMO-ACH-003', 'Farine de blé - Démo', 15.000, 'kg', 750.00),
    ('BRISH-DEMO-ACH-003', 'Lait - Démo', 8.000, 'L', 950.00),
    ('BRISH-DEMO-ACH-003', 'Sucre - Démo', 8.000, 'kg', 800.00)
) AS d(facture, nom_ingredient, quantite, unite, prix)
JOIN achats a ON a.numero_facture = d.facture
JOIN ingredients i ON i.nom = d.nom_ingredient;

UPDATE mets m
SET description = d.description,
    prix_vente_actuel = d.prix,
    actif = TRUE
FROM (VALUES
    ('Poulet braisé (démo)', 'Poulet braisé accompagné de riz local.', 3500.00),
    ('Riz au poisson (démo)', 'Riz cuisiné aux tomates, poisson frais et épices.', 3000.00),
    ('Haricots sautés (démo)', 'Haricots verts sautés à l''oignon et à la tomate.', 2200.00),
    ('Crêpes sucrées (démo)', 'Crêpes légères au lait et au sucre.', 1500.00)
) AS d(nom, description, prix)
WHERE m.nom = d.nom;

INSERT INTO mets (nom, description, prix_vente_actuel, actif)
SELECT d.nom, d.description, d.prix, TRUE
FROM (VALUES
    ('Poulet braisé (démo)', 'Poulet braisé accompagné de riz local.', 3500.00),
    ('Riz au poisson (démo)', 'Riz cuisiné aux tomates, poisson frais et épices.', 3000.00),
    ('Haricots sautés (démo)', 'Haricots verts sautés à l''oignon et à la tomate.', 2200.00),
    ('Crêpes sucrées (démo)', 'Crêpes légères au lait et au sucre.', 1500.00)
) AS d(nom, description, prix)
WHERE NOT EXISTS (SELECT 1 FROM mets m WHERE m.nom = d.nom);

UPDATE recettes r
SET rendement_portions = 10,
    date_effet = CURRENT_DATE - 30,
    active = TRUE
FROM mets m
WHERE r.id_met = m.id_met
  AND r.version = 1
  AND m.nom IN (
      'Poulet braisé (démo)', 'Riz au poisson (démo)',
      'Haricots sautés (démo)', 'Crêpes sucrées (démo)'
  );

INSERT INTO recettes (id_met, version, rendement_portions, date_effet, active)
SELECT m.id_met, 1, 10, CURRENT_DATE - 30, TRUE
FROM mets m
WHERE m.nom IN (
    'Poulet braisé (démo)', 'Riz au poisson (démo)',
    'Haricots sautés (démo)', 'Crêpes sucrées (démo)'
)
  AND NOT EXISTS (
      SELECT 1 FROM recettes r WHERE r.id_met = m.id_met AND r.version = 1
  );

DELETE FROM lignes_recette lr
USING recettes r, mets m
WHERE lr.id_recette = r.id_recette
  AND r.id_met = m.id_met
  AND r.version = 1
  AND m.nom IN (
      'Poulet braisé (démo)', 'Riz au poisson (démo)',
      'Haricots sautés (démo)', 'Crêpes sucrées (démo)'
  );

INSERT INTO lignes_recette (id_recette, id_ingredient, quantite, unite)
SELECT r.id_recette, i.id_ingredient, d.quantite, d.unite
FROM (VALUES
    ('Poulet braisé (démo)', 'Poulet fermier - Démo', 2.500, 'kg'),
    ('Poulet braisé (démo)', 'Riz local - Démo', 1.000, 'kg'),
    ('Poulet braisé (démo)', 'Oignon - Démo', 0.300, 'kg'),
    ('Poulet braisé (démo)', 'Huile végétale - Démo', 0.150, 'L'),
    ('Poulet braisé (démo)', 'Sel - Démo', 0.050, 'kg'),
    ('Poulet braisé (démo)', 'Piment - Démo', 0.020, 'kg'),
    ('Riz au poisson (démo)', 'Poisson frais - Démo', 2.000, 'kg'),
    ('Riz au poisson (démo)', 'Riz local - Démo', 1.500, 'kg'),
    ('Riz au poisson (démo)', 'Tomate - Démo', 0.500, 'kg'),
    ('Riz au poisson (démo)', 'Oignon - Démo', 0.400, 'kg'),
    ('Riz au poisson (démo)', 'Huile végétale - Démo', 0.200, 'L'),
    ('Riz au poisson (démo)', 'Sel - Démo', 0.050, 'kg'),
    ('Haricots sautés (démo)', 'Haricots verts - Démo', 2.000, 'kg'),
    ('Haricots sautés (démo)', 'Tomate - Démo', 0.500, 'kg'),
    ('Haricots sautés (démo)', 'Oignon - Démo', 0.300, 'kg'),
    ('Haricots sautés (démo)', 'Huile végétale - Démo', 0.200, 'L'),
    ('Haricots sautés (démo)', 'Sel - Démo', 0.040, 'kg'),
    ('Crêpes sucrées (démo)', 'Farine de blé - Démo', 1.500, 'kg'),
    ('Crêpes sucrées (démo)', 'Lait - Démo', 1.000, 'L'),
    ('Crêpes sucrées (démo)', 'Sucre - Démo', 0.300, 'kg'),
    ('Crêpes sucrées (démo)', 'Huile végétale - Démo', 0.100, 'L')
) AS d(nom_met, nom_ingredient, quantite, unite)
JOIN mets m ON m.nom = d.nom_met
JOIN recettes r ON r.id_met = m.id_met AND r.version = 1
JOIN ingredients i ON i.nom = d.nom_ingredient;

WITH demonstrations(nom_met, jours, portions_prevues, portions_reelles, statut) AS (
    VALUES
        ('Poulet braisé (démo)', -4, 10, 9, 'TERMINEE'),
        ('Riz au poisson (démo)', -3, 10, 10, 'TERMINEE'),
        ('Haricots sautés (démo)', -2, 10, 8, 'TERMINEE'),
        ('Crêpes sucrées (démo)', -1, 10, 10, 'TERMINEE'),
        ('Poulet braisé (démo)', 0, 12, NULL, 'BROUILLON')
)
INSERT INTO productions (
    id_recette, id_utilisateur, date_production, date_cloture,
    portions_prevues, portions_reelles, statut
)
SELECT r.id_recette, NULL, CURRENT_DATE + d.jours + TIME '11:30',
       CASE WHEN d.statut = 'TERMINEE' THEN CURRENT_DATE + d.jours ELSE NULL END,
       d.portions_prevues, d.portions_reelles, d.statut
FROM demonstrations d
JOIN mets m ON m.nom = d.nom_met
JOIN recettes r ON r.id_met = m.id_met AND r.version = 1
WHERE NOT EXISTS (
    SELECT 1
    FROM productions p
    WHERE p.id_recette = r.id_recette
      AND p.date_production = CURRENT_DATE + d.jours + TIME '11:30'
);

INSERT INTO lignes_production (
    id_production, id_ingredient, quantite_reelle, unite,
    cout_unitaire_applique, cout_total
)
SELECT p.id_production,
       lr.id_ingredient,
       ROUND((lr.quantite * p.portions_reelles / r.rendement_portions)::numeric, 3),
       i.unite_base,
       c.cout_unitaire,
       ROUND(
           ROUND((lr.quantite * p.portions_reelles / r.rendement_portions)::numeric, 3)
           * c.cout_unitaire,
           2
       )
FROM productions p
JOIN recettes r ON r.id_recette = p.id_recette
JOIN mets m ON m.id_met = r.id_met
JOIN lignes_recette lr ON lr.id_recette = r.id_recette
JOIN ingredients i ON i.id_ingredient = lr.id_ingredient
CROSS JOIN LATERAL (
    SELECT ROUND(SUM(la.quantite * la.prix_unitaire) / NULLIF(SUM(la.quantite), 0), 2)
        AS cout_unitaire
    FROM lignes_achat la
    JOIN achats a ON a.id_achat = la.id_achat
    WHERE la.id_ingredient = i.id_ingredient
      AND a.date_achat < p.date_production::date
) c
WHERE m.nom IN (
    'Poulet braisé (démo)', 'Riz au poisson (démo)',
    'Haricots sautés (démo)', 'Crêpes sucrées (démo)'
)
  AND p.statut = 'TERMINEE'
  AND NOT EXISTS (
      SELECT 1 FROM lignes_production lp
      WHERE lp.id_production = p.id_production
        AND lp.id_ingredient = lr.id_ingredient
  );

INSERT INTO historique_couts (
    id_production, cout_matiere, cout_charges, cout_total, cout_unitaire
)
SELECT p.id_production,
       totaux.cout_matiere,
       0.00,
       totaux.cout_matiere,
       ROUND(totaux.cout_matiere / p.portions_reelles, 2)
FROM productions p
JOIN recettes r ON r.id_recette = p.id_recette
JOIN mets m ON m.id_met = r.id_met
CROSS JOIN LATERAL (
    SELECT COALESCE(SUM(lp.cout_total), 0.00) AS cout_matiere
    FROM lignes_production lp
    WHERE lp.id_production = p.id_production
) totaux
WHERE m.nom IN (
    'Poulet braisé (démo)', 'Riz au poisson (démo)',
    'Haricots sautés (démo)', 'Crêpes sucrées (démo)'
)
  AND p.statut = 'TERMINEE'
  AND p.portions_reelles > 0
  AND NOT EXISTS (
      SELECT 1 FROM historique_couts h WHERE h.id_production = p.id_production
  );

COMMIT;
