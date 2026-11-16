# Étape 1 : Compilation de Brish avec Maven et Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.7.4:jar -DoutputDirectory=/app -Dmdep.stripVersion=true

# Étape 2 : Image de production WildFly
FROM quay.io/wildfly/wildfly:latest-jdk17

USER root
RUN mkdir -p /opt/jboss/wildfly/brish && chown -R jboss:jboss /opt/jboss/wildfly/brish

# Tout copier dans le dossier permanent /opt/jboss/wildfly/brish/ (et JAMAIS dans /tmp)
COPY --from=build /app/postgresql.jar /opt/jboss/wildfly/brish/postgresql.jar
COPY docker/configure-wildfly.cli /opt/jboss/wildfly/brish/configure-wildfly.cli
COPY --from=build /app/target/*.war /opt/jboss/wildfly/brish/ROOT.war
COPY docker/start-wildfly.sh /opt/jboss/wildfly/brish/start-wildfly.sh

# Corriger les fins de ligne Windows (CRLF -> LF) et donner les droits d'exécution
RUN sed -i 's/\r$//' /opt/jboss/wildfly/brish/start-wildfly.sh && \
    chmod +x /opt/jboss/wildfly/brish/start-wildfly.sh && \
    chown -R jboss:jboss /opt/jboss/wildfly/brish

USER jboss

# Lancer depuis le dossier permanent
ENTRYPOINT ["/opt/jboss/wildfly/brish/start-wildfly.sh"]
