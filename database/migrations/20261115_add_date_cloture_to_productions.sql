BEGIN;

ALTER TABLE productions
    ADD COLUMN IF NOT EXISTS date_cloture DATE;

UPDATE productions
SET date_cloture = date_production::date
WHERE statut = 'TERMINEE'
  AND date_cloture IS NULL;

COMMIT;
