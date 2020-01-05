FROM python:3.7.4-alpine

WORKDIR /usr/src/crm

# Prevents Python from writing pyc files to disc
ENV PYTHONDONTWRITEBYTECODE 1
# Prevents Python from buffering stdout and stderr
ENV PYTHONUNBUFFERED 1
ENV DJANGO_SETTINGS_MODULE "config.settings.local"

COPY ./requirements/base.txt /usr/src/crm/base.txt
COPY ./requirements/local.txt /usr/src/crm/local.txt

RUN pip install --upgrade pip
RUN \
 apk update && \
 apk add libffi-dev && \
 apk add --no-cache postgresql-libs && \
 apk add --no-cache --virtual build-deps gcc musl-dev postgresql-dev python3-dev && \
 python3 -m pip install -r local.txt --no-cache-dir && \
 apk --purge del build-deps

COPY . /usr/src/crm/

# Check PostgreSQL is running and run Django project
ENTRYPOINT ["/usr/src/crm/dockerizing/app/entrypoint.sh"]