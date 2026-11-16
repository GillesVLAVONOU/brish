CREATE TABLE IF NOT EXISTS autorisations_role (
    role VARCHAR(50) NOT NULL
        CHECK (role IN ('ADMINISTRATEUR', 'GESTIONNAIRE', 'CUISINIER')),
    module VARCHAR(32) NOT NULL
        CHECK (module IN (
            'DASHBOARD', 'INGREDIENTS', 'FOURNISSEURS', 'ACHATS', 'METS',
            'RECETTES', 'PRODUCTIONS', 'RAPPORTS', 'PARAMETRES'
        )),
    peut_consulter BOOLEAN NOT NULL DEFAULT FALSE,
    peut_modifier BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (role, module),
    CHECK (NOT peut_modifier OR peut_consulter)
);

CREATE TABLE IF NOT EXISTS autorisations_utilisateur (
    id_utilisateur INTEGER NOT NULL REFERENCES utilisateurs(id_utilisateur) ON DELETE CASCADE,
    module VARCHAR(32) NOT NULL
        CHECK (module IN (
            'DASHBOARD', 'INGREDIENTS', 'FOURNISSEURS', 'ACHATS', 'METS',
            'RECETTES', 'PRODUCTIONS', 'RAPPORTS', 'PARAMETRES'
        )),
    peut_consulter BOOLEAN,
    peut_modifier BOOLEAN,
    PRIMARY KEY (id_utilisateur, module),
    CHECK (peut_modifier IS DISTINCT FROM TRUE OR peut_consulter IS DISTINCT FROM FALSE)
);

INSERT INTO autorisations_role (role, module, peut_consulter, peut_modifier)
SELECT role, module, consultation, modification
FROM (VALUES
    ('ADMINISTRATEUR', 'DASHBOARD', TRUE, TRUE),
    ('ADMINISTRATEUR', 'INGREDIENTS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'FOURNISSEURS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'ACHATS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'METS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'RECETTES', TRUE, TRUE),
    ('ADMINISTRATEUR', 'PRODUCTIONS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'RAPPORTS', TRUE, TRUE),
    ('ADMINISTRATEUR', 'PARAMETRES', TRUE, TRUE),
    ('GESTIONNAIRE', 'DASHBOARD', TRUE, TRUE),
    ('GESTIONNAIRE', 'INGREDIENTS', TRUE, TRUE),
    ('GESTIONNAIRE', 'FOURNISSEURS', TRUE, TRUE),
    ('GESTIONNAIRE', 'ACHATS', TRUE, TRUE),
    ('GESTIONNAIRE', 'METS', TRUE, TRUE),
    ('GESTIONNAIRE', 'RECETTES', TRUE, TRUE),
    ('GESTIONNAIRE', 'PRODUCTIONS', TRUE, TRUE),
    ('GESTIONNAIRE', 'RAPPORTS', TRUE, TRUE),
    ('GESTIONNAIRE', 'PARAMETRES', TRUE, TRUE),
    ('CUISINIER', 'DASHBOARD', FALSE, FALSE),
    ('CUISINIER', 'INGREDIENTS', TRUE, FALSE),
    ('CUISINIER', 'FOURNISSEURS', FALSE, FALSE),
    ('CUISINIER', 'ACHATS', FALSE, FALSE),
    ('CUISINIER', 'METS', TRUE, FALSE),
    ('CUISINIER', 'RECETTES', TRUE, FALSE),
    ('CUISINIER', 'PRODUCTIONS', TRUE, TRUE),
    ('CUISINIER', 'RAPPORTS', FALSE, FALSE),
    ('CUISINIER', 'PARAMETRES', FALSE, FALSE)
) AS droits(role, module, consultation, modification)
ON CONFLICT (role, module) DO NOTHING;

INSERT INTO autorisations_utilisateur (id_utilisateur, module, peut_consulter, peut_modifier)
SELECT u.id_utilisateur, modules.module, TRUE, TRUE
FROM utilisateurs u
CROSS JOIN (VALUES
    ('DASHBOARD'), ('INGREDIENTS'), ('FOURNISSEURS'), ('ACHATS'), ('METS'),
    ('RECETTES'), ('PRODUCTIONS'), ('RAPPORTS'), ('PARAMETRES')
) AS modules(module)
ON CONFLICT (id_utilisateur, module) DO NOTHING;
