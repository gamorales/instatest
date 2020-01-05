import os

from .base import *

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = env('SECRET_KEY')

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = env.list('ALLOWED_HOSTS')

# Database
# https://docs.djangoproject.com/en/3.0/ref/settings/#databases

DATABASES = {"default": env.db("DATABASE_URL")}

INTERNAL_IPS = [
    '127.0.0.1',
]
