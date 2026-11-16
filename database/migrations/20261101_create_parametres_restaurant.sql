CREATE TABLE IF NOT EXISTS parametres_restaurant (
    id_parametres SMALLINT PRIMARY KEY CHECK (id_parametres = 1),
    nom_restaurant VARCHAR(120) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(30),
    code_devise CHAR(3) NOT NULL DEFAULT 'XOF'
        CHECK (code_devise IN ('XOF', 'XAF', 'NGN', 'GHS', 'EUR', 'USD'))
);

INSERT INTO parametres_restaurant (id_parametres, nom_restaurant, adresse, code_devise)
VALUES (1, 'Le Jardin', 'Cotonou', 'XOF')
ON CONFLICT (id_parametres) DO NOTHING;
