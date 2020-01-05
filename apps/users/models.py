#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import unicode_literals
from django.db import models
from django.contrib.auth.models import AbstractBaseUser, PermissionsMixin, BaseUserManager


class UserManager(BaseUserManager):

    def _create_user(self, email, password, **extra_fields):
        if not email:
            raise ValueError('The given email must be set')
        email = self.normalize_email(email)
        try:
            user = self.get(email=email)
        except User.DoesNotExist:
            user = self.create(email=email, **extra_fields)
            user.set_password(password)
            user.save(using=self._db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        """ Create a user

        Args:
            email:
            password:
            extra_fields: Flags to update the user as an superuser or staff.

        Returns:
            Object of the user created

        Raises:
            User object created
        """
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        extra_fields.setdefault('is_active', True)
        extra_fields.setdefault('is_online', False)
        extra_fields.setdefault('is_verificated', False)
        return self._create_user(email, password, **extra_fields)

    def create_superuser(self, email, password, **extra_fields):
        """ Create an administrator

        With this method will be created a superuser with these flags at True
            * is_staff
            * is_superuser
            * is_active
            * is_online
            * is_verificated

        Args:
            email:
            password:
            extra_fields: Not valid

        Returns:
            Object of the user created
        """
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('is_active', True)
        extra_fields.setdefault('is_online', False)
        extra_fields.setdefault('is_verificated', True)

        # Validate the extra fields
        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')
        if extra_fields.get('is_active') is not True:
            raise ValueError('Superuser must have is_active=True.')
        if extra_fields.get('is_verificated') is not True:
            raise ValueError('Superuser must have is_verificated=True.')
        return self._create_user(email, password, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):

    objects = UserManager()

    email = models.EmailField(unique=True, error_messages={'unique': 'The email is already registered'})
    first_name = models.CharField(max_length=150)
    last_name = models.CharField(max_length=150)

    date_joined = models.DateTimeField(auto_now_add=True)
    last_online = models.DateTimeField(blank=True, null=True)
    last_login = models.DateTimeField(blank=True, null=True)
    recovery_code = models.UUIDField(blank=True, null=True)
    activate_code = models.UUIDField(blank=True, null=True)

    is_online = models.BooleanField(default=False)
    is_active = models.BooleanField(default=False)
    is_staff = models.BooleanField(default=False)
    is_superuser = models.BooleanField(default=False)
    is_verificated = models.BooleanField(default=False)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['first_name', 'last_name']

    class Meta:
        ordering = ['first_name']
        verbose_name = 'User'
        verbose_name_plural = 'Users'

    def get_full_name(self):
        return f"{self.first_name} {self.last_name}"
