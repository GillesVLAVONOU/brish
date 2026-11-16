# Étape 1 : Builder l'application avec Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Lancer le serveur WildFly de production
FROM quay.io/wildfly/wildfly:latest-jdk17

# Copier le fichier .war généré automatiquement dans WildFly
COPY --from=build /app/target/*.war /opt/jboss/wildfly/standalone/deployments/

# Exposer le port par défaut de WildFly
EXPOSE 8080

# Lancer WildFly en mode standalone lié à toutes les interfaces réseau
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
