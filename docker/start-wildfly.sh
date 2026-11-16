#!/bin/sh
set -eu

PORT="${PORT:-10000}"

# Optimisations JVM pour démarrer vite sur 0.1 CPU et 512 Mo RAM
export JAVA_OPTS="-Xms128m -Xmx224m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=192m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss256k -Djava.net.preferIPv4Stack=true"
export JAVA_OPTS_APPEND="$JAVA_OPTS"

exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Djboss.http.port="$PORT"
