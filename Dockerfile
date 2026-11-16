# Étape 1 : Compilation de Brish avec Maven et Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.7.4:jar -DoutputDirectory=/app -Dmdep.stripVersion=true

# Étape 2 : Image de production WildFly pré-configurée
FROM quay.io/wildfly/wildfly:latest-jdk17

USER root
RUN mkdir -p /opt/jboss/wildfly/brish && chown -R jboss:jboss /opt/jboss/wildfly/brish

COPY --from=build /app/postgresql.jar /opt/jboss/wildfly/brish/postgresql.jar
COPY docker/configure-wildfly.cli /opt/jboss/wildfly/brish/configure-wildfly.cli
COPY docker/start-wildfly.sh /opt/jboss/wildfly/brish/start-wildfly.sh

RUN sed -i 's/\r$//' /opt/jboss/wildfly/brish/start-wildfly.sh && \
    chmod +x /opt/jboss/wildfly/brish/start-wildfly.sh && \
    chown -R jboss:jboss /opt/jboss/wildfly/brish

USER jboss

# Exécuter la configuration UNE SEULE FOIS pendant le build (avec résolution dynamique activée)
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/brish/configure-wildfly.cli && \
    rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current/*

# Déployer directement ROOT.war
COPY --from=build /app/target/*.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

ENTRYPOINT ["/opt/jboss/wildfly/brish/start-wildfly.sh"]
