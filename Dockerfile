# Étape 1 : Compilation de Brish avec Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Configuration et exécution sur WildFly
FROM quay.io/wildfly/wildfly:latest-jdk17

# Copier le driver PostgreSQL et le script CLI
COPY docker/postgresql.jar /tmp/postgresql.jar
COPY docker/configure-wildfly.cli /tmp/configure-wildfly.cli

# Exécuter la configuration JNDI dans WildFly
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure-wildfly.cli && \
    rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current/*

# Copier l'application Brish générée dans WildFly
COPY --from=build /app/target/*.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

# Lancer WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
