# InstaTest

## Local Development Environment

1. Install docker: https://docs.docker.com/install/

2. Run docker test or hello-world to know if everything ok
```
docker exec hello-world
```

3. Install docker-compose: https://docs.docker.com/compose/install/

4. Test the installation: 
```
docker-compose --version
```

5. Compile docker to install
```
docker build .
```

6. Compile docker-compose
```
docker-compose build
```

7. Start a project
```
docker-compose exec <service-name> sh -c "django-admin.py startproject <project-name> ."
```

8. Create an application
```
docker-compose exec <service-name> python manage.py startapp <app-name>;
docker-compose exec <service-name> sh -c "manage.py startapp <app-name>"
```

9. Migrate tables
```
docker-compose exec <service-name> python manage.py makemigrations;
docker-compose exec <service-name> python manage.py migrate
```

10. Create superuser
```
docker-compose exec <service-name> python manage.py createsuperuser
```

11. Compile development [option -d run background]
```
docker-compose up --build
```

### Environment variables needed to docker works
```
SECRET_KEY=<random character>
DATABASE_URL=psql://<database_user>:<database_password>@<host>:<port>/<database_name>

POSTGRES_ENGINE=django.db.backends.postgresql
POSTGRES_HOST=<host>
POSTGRES_PORT=<port>
POSTGRES_DB=<database_name>
POSTGRES_USER=<database_user>
POSTGRES_PASSWORD=<database_password>
DATABASE=<docker-service-name>
```