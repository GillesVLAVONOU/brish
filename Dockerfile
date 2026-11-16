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

COPY --from=build /app/postgresql.jar /tmp/postgresql.jar
COPY --from=build /app/target/Brish-1.0-SNAPSHOT.war /tmp/ROOT.war
COPY docker/configure-wildfly.cli /tmp/configure-wildfly.cli
COPY docker/start-wildfly.sh /tmp/start-wildfly.sh

EXPOSE 10000
CMD ["/bin/sh", "/tmp/start-wildfly.sh"]
