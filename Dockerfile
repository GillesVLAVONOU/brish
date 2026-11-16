# Étape 1 : Compilation de Brish avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17
RUN mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.7.4:jar -DoutputDirectory=/app -Dmdep.stripVersion=true

# Étape 2 : Configuration et exécution sur WildFly
FROM quay.io/wildfly/wildfly:latest-jdk17

COPY --from=build /app/postgresql.jar /tmp/postgresql.jar
COPY docker/configure-wildfly.cli /tmp/configure-wildfly.cli

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure-wildfly.cli && \
    rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current/*

COPY --from=build /app/target/*.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
