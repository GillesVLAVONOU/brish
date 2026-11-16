\echo 'Création de la base Brish et application du schéma défini par le diagramme.'
SELECT 'CREATE DATABASE brish WITH ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'brish')
\gexec

\connect brish
\ir brish_analyse.sql
\ir migrations/20261101_create_autorisations.sql
\ir migrations/20261115_add_date_cloture_to_productions.sql
\ir migrations/20261115_create_sales.sql
