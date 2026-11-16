# Étape 1 : Compilation de Brish avec Maven et Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.7.4:jar -DoutputDirectory=/app -Dmdep.stripVersion=true

# Étape 2 : Image de production WildFly
FROM quay.io/wildfly/wildfly:latest-jdk17

# Créer le dossier brish attendu par votre script start
USER root
RUN mkdir -p /opt/jboss/wildfly/brish && chown -R jboss:jboss /opt/jboss/wildfly/brish

# Copier le driver PostgreSQL, le script CLI, le WAR et votre script de démarrage
COPY --from=build /app/postgresql.jar /tmp/postgresql.jar
COPY docker/configure-wildfly.cli /opt/jboss/wildfly/brish/configure-wildfly.cli
COPY --from=build /app/target/*.war /opt/jboss/wildfly/brish/ROOT.war
COPY docker/start-wildfly.sh /tmp/start-wildfly.sh

# Corriger les fins de ligne Windows (CRLF -> LF) et rendre exécutable
RUN sed -i 's/\r$//' /tmp/start-wildfly.sh && \
    chmod +x /tmp/start-wildfly.sh && \
    chown -R jboss:jboss /tmp/postgresql.jar /tmp/start-wildfly.sh /opt/jboss/wildfly/brish

USER jboss

# Lancer votre script de démarrage
ENTRYPOINT ["/tmp/start-wildfly.sh"]
