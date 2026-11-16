#!/bin/sh
set -eu

: "${BRISH_DB_URL:?Configure BRISH_DB_URL in Render}"
: "${BRISH_DB_USER:?Configure BRISH_DB_USER in Render}"
: "${BRISH_DB_PASSWORD:?Configure BRISH_DB_PASSWORD in Render}"

case "$BRISH_DB_URL" in
    jdbc:postgresql://*) ;;
    *)
        echo "BRISH_DB_URL must use the jdbc:postgresql:// format." >&2
        exit 1
        ;;
esac

PORT="${PORT:-10000}"
case "$PORT" in
    ''|*[!0-9]*)
        echo "PORT must be a numeric value." >&2
        exit 1
        ;;
esac

JBOSS_HOME="${JBOSS_HOME:-/opt/jboss/wildfly}"
CONFIG_MARKER="$JBOSS_HOME/standalone/configuration/.brish-datasource-configured"

if [ ! -f "$CONFIG_MARKER" ]; then
    "$JBOSS_HOME/bin/jboss-cli.sh" --file=/tmp/configure-wildfly.cli
    touch "$CONFIG_MARKER"
fi

cp /tmp/ROOT.war "$JBOSS_HOME/standalone/deployments/ROOT.war"
exec "$JBOSS_HOME/bin/standalone.sh" -b 0.0.0.0 -Djboss.http.port="$PORT"
