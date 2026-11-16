ALTER TABLE lignes_production
    ADD COLUMN IF NOT EXISTS unite VARCHAR(20);

UPDATE lignes_production ligne
SET unite = ingredient.unite_base
FROM ingredients ingredient
WHERE ingredient.id_ingredient = ligne.id_ingredient
  AND ligne.unite IS NULL;

ALTER TABLE lignes_production
    ALTER COLUMN unite SET NOT NULL;
