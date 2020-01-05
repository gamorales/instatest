FROM python:3.7.4-alpine

WORKDIR /usr/src/app

# Prevents Python from writing pyc files to disc
ENV PYTHONDONTWRITEBYTECODE 1
# Prevents Python from buffering stdout and stderr
ENV PYTHONUNBUFFERED 1
ENV DJANGO_SETTINGS_MODULE "config.settings.local"

COPY ./requirements/base.txt /usr/src/app/base.txt
COPY ./requirements/local.txt /usr/src/app/local.txt

RUN pip install --upgrade pip
RUN \
 apk update && \
 apk add libffi-dev && \
 apk add --no-cache postgresql-libs && \
 apk add --no-cache --virtual build-deps gcc musl-dev postgresql-dev python3-dev && \
 python3 -m pip install -r local.txt --no-cache-dir && \
 apk --purge del build-deps

COPY . /usr/src/app/

# Check PostgreSQL is running and run Django project
ENTRYPOINT ["/usr/src/app/dockerizing/app/entrypoint.sh"]