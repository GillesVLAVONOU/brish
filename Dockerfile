FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
RUN mvn -B clean package
RUN mvn -B dependency:copy \
    -Dartifact=org.postgresql:postgresql:42.7.7:jar \
    -DoutputDirectory=/app \
    -Dmdep.stripVersion=true

FROM quay.io/wildfly/wildfly:41.0.1.Final-jdk25
ENV JBOSS_HOME=/opt/jboss/wildfly

COPY --from=build /app/postgresql.jar /opt/jboss/wildfly/brish/postgresql.jar
COPY --from=build /app/target/Brish-1.0-SNAPSHOT.war /opt/jboss/wildfly/brish/ROOT.war
COPY docker/configure-wildfly.cli /opt/jboss/wildfly/brish/configure-wildfly.cli
COPY docker/start-wildfly.sh /opt/jboss/wildfly/brish/start-wildfly.sh

EXPOSE 10000
CMD ["/bin/sh", "/opt/jboss/wildfly/brish/start-wildfly.sh"]
