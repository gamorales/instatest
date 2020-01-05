#!/bin/sh

set -o errexit
set -o pipefail
set -o nounset

# N.B. If only .env files supported variable expansion...
# export CELERY_BROKER_URL="${REDIS_URL}"

if [ -z "${POSTGRES_USER}" ]; then
    base_postgres_image_default_user='postgres'
    export POSTGRES_USER="${base_postgres_image_default_user}"
fi
export DATABASE_URL="postgres://${POSTGRES_USER}:${POSTGRES_PASSWORD}@${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}"
#export DATABASE_URL="${DATABASE_URL}"

postgres_ready() {
python << END
import sys
import psycopg2
try:
    psycopg2.connect(
        dbname="${POSTGRES_DB}",
        user="${POSTGRES_USER}",
        password="${POSTGRES_PASSWORD}",
        host="${POSTGRES_HOST}",
        port="${POSTGRES_PORT}",
    )
except psycopg2.OperationalError:
    sys.exit(-1)
sys.exit(0)
END
}
until postgres_ready; do
  >&2 echo "Waiting for PostgreSQL to become available... $DATABASE_URL"
  sleep 1
done
>&2 echo 'PostgreSQL is available'

# python manage.py collectstatic --no-input --clear
python manage.py migrate
python manage.py runserver 0.0.0.0:8000

exec "$@"
