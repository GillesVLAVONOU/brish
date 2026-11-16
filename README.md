# Suivi des coûts du restaurant

Application web Java basée sur Maven, Jakarta EE et JSP pour suivre les achats, recettes, productions et coûts d'un restaurant.

## Structure

Le code Java est dans `src/main/java/bj/restaurant/couts`, les vues JSP dans `src/main/webapp/views` et les ressources web dans `src/main/webapp/assets`.

## Configuration de la base de données

PostgreSQL 18 est attendu sur `localhost:5432`. Le schéma de référence est défini dans `database/brish_analyse.sql`. `database/init.sql` crée la base si nécessaire puis applique ce schéma.

Sur Windows, depuis la racine du projet, initialiser la base et ses tables :

```powershell
.\tools\init-database.ps1
```

Le script demande le mot de passe du rôle PostgreSQL `postgres`, puis exige la confirmation `REINITIALISER BRISH`. **Cette opération supprime les tables et toutes leurs données existantes.** Pour connecter l'application, définir les variables d'environnement avant de démarrer le serveur :

```powershell
$env:BRISH_DB_URL = "jdbc:postgresql://localhost:5432/brish"
$env:BRISH_DB_USER = "postgres"
$env:BRISH_DB_PASSWORD = "votre-mot-de-passe"
```

`DatabaseConnection` lit ces variables en priorité, puis les propriétés système et `src/main/resources/database.properties`. Ne commitez jamais un vrai mot de passe dans ce fichier.

Pour remplir une base déjà initialisée avec des données de présentation, exécuter :

```powershell
.\tools\seed-demo.ps1
```

Le script demande le mot de passe PostgreSQL de façon masquée si `BRISH_DB_PASSWORD` n'est pas défini. Il applique les migrations idempotentes requises, puis ajoute des fournisseurs, ingrédients, achats, recettes et productions de démonstration, sans créer de compte utilisateur ni réinitialiser les données existantes. Les enregistrements concernés sont identifiés par « démo » ou par le préfixe `BRISH-DEMO-`.

### JPA et WildFly

L'unité de persistance s'appelle `BrishPU` et utilise le datasource JTA `java:/jdbc/BrishDS`. Avant de déployer l'application, configurez ce datasource dans WildFly pour pointer vers la base `brish` et enregistrez le pilote PostgreSQL côté serveur. Le pilote présent comme dépendance Maven ne configure pas à lui seul le datasource WildFly.

Les entités JPA correspondent aux tables de `database/brish_analyse.sql` et `Role` est enregistré comme une chaîne. La génération automatique du schéma est désactivée ; `database/init.sql` est une réinitialisation destructive, pas une migration des données existantes. Pour une base déjà initialisée, appliquer manuellement les scripts non destructifs de `database/migrations/` avant de redéployer, dans l'ordre : `20261030_add_unite_lignes_production.sql`, `20261101_create_parametres_restaurant.sql`, `20261101_create_autorisations.sql`, `20261101_create_password_reset_tokens.sql`, `20261115_add_date_cloture_to_productions.sql` puis `20261115_create_sales.sql`. Cette dernière crée les tables de ventes et ajoute le module VENTES aux autorisations (Gestionnaire et Administrateur par défaut). La migration des autorisations conserve les droits opérationnels actuels des comptes existants lors de sa première application ; les nouveaux comptes héritent de leur rôle, et les comptes existants peuvent être remis en héritage depuis l'administration. Ne réexécutez pas `database/init.sql` sur une base à conserver. Avec les variables `BRISH_DB_USER` et `BRISH_DB_PASSWORD` définies localement, les migrations peuvent être appliquées sous PowerShell ainsi :

```powershell
if ([string]::IsNullOrWhiteSpace($env:BRISH_DB_USER) -or [string]::IsNullOrWhiteSpace($env:BRISH_DB_PASSWORD)) {
    throw "Définissez localement BRISH_DB_USER et BRISH_DB_PASSWORD avant la migration."
}
$env:PGPASSWORD = $env:BRISH_DB_PASSWORD
try {
    psql -h localhost -p 5432 -U $env:BRISH_DB_USER -d brish -v ON_ERROR_STOP=1 `
        -f database\migrations\20261101_create_parametres_restaurant.sql `
        -f database\migrations\20261101_create_autorisations.sql `
        -f database\migrations\20261101_create_password_reset_tokens.sql `
        -f database\migrations\20261115_add_date_cloture_to_productions.sql `
        -f database\migrations\20261115_create_sales.sql
    if ($LASTEXITCODE -ne 0) { throw "Échec de la migration PostgreSQL." }
} finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
```

Les autorisations sont vérifiées côté serveur pour les requêtes de consultation et d'écriture. Dans Administration, l'administrateur peut régler les droits de base des rôles Gestionnaire et Cuisinier, puis définir pour chaque compte des exceptions qui autorisent, refusent ou héritent de la matrice. Une modification inclut la consultation. Les droits du rôle Administrateur restent complets et non modifiables ; l'accès à la gestion des utilisateurs est réservé à ce rôle. Par défaut, un nouveau Gestionnaire peut accéder à tous les modules métier ; un nouveau Cuisinier peut consulter les ingrédients, mets et recettes, et gérer les productions. Le tableau de bord, les achats, fournisseurs, rapports et paramètres ne sont pas accessibles au rôle Cuisinier par défaut.

Les modules Ingrédients, Fournisseurs, Achats, Mets & recettes et Productions sont raccordés à `BrishPU`. Les achats enregistrent les factures et leurs lignes sans modifier le stock ni les coûts courants. Les recettes sont versionnées et utilisent l'unité de base de chaque ingrédient. Les productions enregistrent les portions prévues puis les portions et consommations réelles à la clôture ; leurs coûts matières utilisent la moyenne pondérée des achats antérieurs dans la même unité de base. Aucune quantité en stock n'est décrémentée.

Les rapports de coûts (par date de production, dernier instantané de coût), d'achats (par date de facture) et de marges (par date de vente) sont calculés à partir des enregistrements réels. Une vente est rattachée à une production clôturée ayant un coût enregistré ; le nombre vendu ne peut dépasser les portions disponibles. Le prix unitaire appliqué est enregistré dans la ligne de vente pour préserver l'historique, et le coût des portions vendues est calculé avec le coût unitaire réel de la production. Le rapport affiche le chiffre d'affaires, le coût associé, la marge brute et son taux. Les paramètres du restaurant (nom, adresse/ville, téléphone) sont partagés dans l'application. La devise peut être choisie parmi XOF, XAF, NGN, GHS, EUR et USD uniquement avant tout enregistrement de met/prix, d'achat ou de coût ; elle est ensuite verrouillée pour ne pas réinterpréter les montants existants. Aucun taux ni conversion de devise n'est appliqué.

Les rapports de coûts, d'achats et de marges peuvent être exportés en PDF avec JasperReports 7.1.1. Les modèles modifiables se trouvent dans `src/main/resources/reports/`; Maven les précompile dans le WAR afin que WildFly n'ait pas besoin d'un compilateur Java au moment de la requête.

L'administration des utilisateurs est réservée aux comptes administrateurs. Elle permet de créer et modifier les comptes, d'attribuer les rôles Administrateur/Gestionnaire/Cuisinier, de définir un mot de passe initial ou de le remplacer, et d'activer/désactiver les comptes sans les supprimer. Un mot de passe doit comporter au moins 12 caractères. La page « Mot de passe oublié » envoie un lien de réinitialisation à usage unique, stocké uniquement sous forme hachée et valable 30 minutes ; les demandes sont limitées à une par compte toutes les deux minutes. Les formulaires sont protégés contre les soumissions CSRF.

Pour activer l'envoi des e-mails, configurez l'environnement du processus WildFly avant son démarrage. N'inscrivez jamais ces valeurs dans le dépôt :

```powershell
$env:BRISH_SMTP_HOST = "smtp.example.com"
$env:BRISH_SMTP_PORT = "587"
$env:BRISH_SMTP_USERNAME = "compte-smtp"
$env:BRISH_SMTP_PASSWORD = "secret-fourni-par-votre-service-mail"
$env:BRISH_SMTP_FROM = "support@example.com"
$env:BRISH_PUBLIC_BASE_URL = "https://votre-domaine/Brish-1.0-SNAPSHOT"
```

Le serveur doit accepter SMTP avec STARTTLS sur le port configuré. `BRISH_PUBLIC_BASE_URL` doit être l'URL HTTPS publique complète de l'application ; HTTP est permis uniquement pour `localhost` en développement. Le processus WildFly doit être redémarré après la configuration des variables. Si ces paramètres ou la migration manquent, l'application affiche explicitement que la récupération est temporairement indisponible.

### Authentification JAAS

Le login utilise le module JAAS `BrishLoginModule` et le datasource `BrishDS`. Les mots de passe sont enregistrés sous forme de hash PBKDF2-HMAC-SHA256 avec un sel aléatoire.

Lorsqu'aucun administrateur n'existe, ouvrir l'application depuis la machine WildFly redirige vers `/setup-admin`. Cette page n'accepte que les connexions locales, demande un mot de passe d'au moins 12 caractères et n'est disponible qu'avant la création du premier administrateur. Après création, connecter l'administrateur sur `/login`.

Les pages applicatives nécessitent une session authentifiée. La déconnexion invalide cette session.

## Compilation

Le projet cible Java 25 LTS. Utilisez un JDK 25 pour compiler et exécuter l'application. Avant de démarrer WildFly, configurez son environnement avec `JAVA_HOME` pointant vers ce JDK. Le script `tools/configure-wildfly-datasource.ps1` utilise `JAVA_HOME` (ou le `java.exe` du `PATH`) et vérifie que sa version majeure est 25.

```bash
mvn clean package
```

Les modules Ingrédients, Fournisseurs, Mets, Productions et Ventes proposent la consultation et la gestion de leurs données. Les achats enregistrent les factures et leurs lignes. Les fiches recettes conservent leurs versions successives. Le suivi des ventes exige que chaque vente corresponde à une production clôturée disposant d'un coût enregistré.
