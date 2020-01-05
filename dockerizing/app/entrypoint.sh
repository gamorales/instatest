#!/bin/sh

if [ "$DATABASE" = "postgres" ]
then
    echo "Waiting for postgres..."

    while ! nc -z $POSTGRES_HOST $POSTGRES_PORT; do
      sleep 1.0
    done

    echo "PostgreSQL ha iniciado con éxito"
fi

#python manage.py flush --no-input
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
python manage.py collectstatic --no-input --clear

exec "$@"