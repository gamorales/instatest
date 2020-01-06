#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import json

from django.views.generic import TemplateView
from django.http import HttpResponse
from django.conf import settings

from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes, parser_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView
from rest_framework import status

from .models import User
from .serializers import UserSerializer


@api_view(['POST', ])
@permission_classes([IsAuthenticated])
def user_data(request):
    """ Retrieves User DTO

    Args:
        request: Request access to consume the API

    Return:
        JSON with the user object
        example:
            {
                "id": 6,
                "email": "info@info.com",
                "first_name": "Guillermo",
                "last_name": "Alfonso",
                "date_joined": "2020-01-02T13:59:45.102786Z",
                "is_superuser": true,
                "is_active": true
            }
    """
    if request.method == 'POST':
        try:
            user = User.objects.get(email=request.data['email'])
            serializer = UserSerializer(user)
            return Response(serializer.data)
        except User.DoesNotExist:
            return Response({"error": True, "message": f"email user {email} no exists"})


@api_view(['GET', ])
@permission_classes([IsAuthenticated])
def get_users_list(request):
    """ Retrieves all Users

    Retrieves all Users ordered by date joined (page every base.REST_FRAMEWORK['PAGE_SIZE'] records), in case of
    error will show a message "Invalid Page when pagination not exists" or a JSON informing token is not valid.
        example:
            {
                "detail": "Given token not valid for any token type",
                "code": "token_not_valid",
                "messages": [
                    {
                        "token_class": "AccessToken",
                        "token_type": "access",
                        "message": "Token is invalid or expired"
                    }
                ]
            }

    Args:
        request: Request access to consume the API

    Return:
        JSON with a list of user objects
        example:
            [
                {
                    "id": 6,
                    "email": "info@info.com",
                    "first_name": "",
                    "last_name": "",
                    "date_joined": "2020-01-02T13:59:45.102786Z",
                    "is_superuser": true,
                    "is_active": true
                },
            ]
    """
    paginator = PageNumberPagination()
    users = User.objects.all().order_by('-date_joined')
    context = paginator.paginate_queryset(users, request)
    serializer = UserSerializer(context, many=True)
    return paginator.get_paginated_response(serializer.data)


@api_view(['GET', ])
@permission_classes([IsAuthenticated])
def get_user(request, pk):
    """ Retrieves single User

    In case the user doesn't exists will show a message informing.

    Args:
        request: Request access to consume the API
        pk: Id of the user to search

    Return:
        JSON with the user object
        example:
            {
                "id": 6,
                "email": "info@info.com",
                "first_name": "",
                "last_name": "",
                "date_joined": "2020-01-02T13:59:45.102786Z",
                "is_superuser": true,
                "is_active": true
            }
    """
    try:
        user = User.objects.get(pk=pk)
        serializer = UserSerializer(user)
        return Response(serializer.data)
    except User.DoesNotExist:
        return Response({"error": True, "message": f"id user {pk} no exists"})


@api_view(['DELETE', ])
@permission_classes([IsAuthenticated])
def delete(request, pk):
    """ Deletes User object

    Delete an User from the model, the master can't be disabled for API purposes, in case the user doesn't exists will
    show a message informing.

    Args:
        request: Request access to consume the API
        pk: Id of the user to search

    Return:
        JSON with status and message if success
    """
    if pk == 6:
        return Response({"error": True, "message": f"This user can't be delete."})

    try:
        user = User.objects.get(pk=pk)
        user.delete()
        return Response(
            {"success": f"User {user.first_name} {user.last_name} has been deleted."},
            status=204
        )
    except User.DoesNotExist:
        return Response({"error": True, "message": f"id user {pk} can't be deleted because doesn't exists"})


@api_view(['PUT', ])
@permission_classes([IsAuthenticated])
def disable(request, pk):
    """ Deactives User object

    Change the flat is_active of the User object, the master can't be disabled for API purposes, in case the user
    doesn't exists will show a message informing.

    Args:
        request: Request access to consume the API
        pk: Id of the user to search

    Return:
        JSON with status and message if success
    """
    if pk == 6:
        return Response({"error": True, "message": f"This user can't be disable."})

    try:
        user_save = User.objects.get(pk=pk)
        serializer = UserSerializer(
            instance=user_save,
            data={},
            partial=True  # To update some fields but not necessarily all at once.
        )
        data = {}
        if serializer.is_valid(raise_exception=True):
            user = serializer.disable(user_save)
            data['success'] = f"User {user.first_name} {user.last_name} has been disabled"
            data['email'] = user.email

        return Response(data)
    except User.DoesNotExist:
        return Response({"error": True, "message": f"id user {pk} can't be disable because doesn't exists"})


@api_view(['PUT', ])
@permission_classes([IsAuthenticated])
def update(request, pk):
    """ Updates User object

    The mains fields email, first_name, last_name and password could be updated, in case the user doesn't exists
    will show a message informing.

    Args:
        request: Request access to consume the API
        pk: Id of the user to search

    Return:
        JSON with status and message if success
    """
    try:
        user_save = User.objects.get(pk=pk)
        serializer = UserSerializer(
            instance=user_save,
            data=request.data,
            partial=True  # To update some fields but not necessarily all at once.
        )
        data = {}
        if serializer.is_valid(raise_exception=True):
            user = serializer.update(user_save)
            data['success'] = f"User {user.first_name} {user.last_name} updated"
            data['email'] = user.email

        return Response(data)
    except User.DoesNotExist:
        return Response({"error": True, "message": f"id user {pk} can't be updated because doesn't exists"})


@api_view(['POST', ])
def registration(request):
    """ Register new User

    If the new user email already exists, the shows a message informing than the email account is registered.

    Args:
        request: Request data with
            * email
            * password
            * confirm_password
            * first_name
            * last_name

    Return:
        JSON with data of the user created
        example:
            {
                "success": "Successfully registered user",
                "email": "algos@algos.edu.co",
                "name": "Marco Polo"
            }
    """
    if request.method == 'POST':
        serializer = UserSerializer(data=request.data)
        data = {}

        if serializer.is_valid():
            user = serializer.save()
            data['success'] = 'Successfully registered user'
            data['email'] = user.email
            data['name'] = f"{user.first_name} {user.last_name}"
        else:
            data = serializer.errors

        return Response(data)


@api_view(['POST', ])
def get_code(request):
    """ Recover User password with code

    Before change User password, a code of 6 digits will be send to the user

    Args:
        request: Request data with
            * email

    Return:
        JSON with the code to change
        example:
            {
                "code": 123456,
            }

    Raises:
        example:
            {
                "error": True,
                "message": "Email with code are wrong!"
            }
    """
    if request.method == 'POST':
        try:
            user = User.objects.get(email=request.data['email'])
            serializer = UserSerializer(
                instance=user,
                data=request.data,
                partial=True  # To update some fields but not necessarily all at once.
            )
            data = {}
            if serializer.is_valid(raise_exception=True):
                user = serializer.code(user)
                data['success'] = f"Code for {user.first_name} {user.last_name}, generated"
                data['email'] = user.email
                data['code'] = user.code

            return Response(data)
        except User.DoesNotExist:
            return Response({"error": True, "message": f"Email {request.data['email']} doesn't exists"})


@api_view(['POST', ])
def reset_password(request):
    """ Recover User password with code

    Before change User password, a code of 6 digits will be send to the user

    Args:
        request: Request data with
            * email

    Return:
        JSON with the code to change
        example:
            {
                "success": "Password updated for user Guillermo Morales.",
                "email": "info@info.com"
            }

    Raises:
        example:
            {
                "error": True,
                "message": "Email with code are wrong!"
            }
    """
    if request.method == 'POST':
        try:
            user = User.objects.get(email=request.data['email'], code=request.data['code'])
            serializer = UserSerializer(
                instance=user,
                data=request.data,
                partial=True  # To update some fields but not necessarily all at once.
            )
            data = {}
            if serializer.is_valid(raise_exception=True):
                user = serializer.reset_password(user)
                data['success'] = f"Password updated for user {user.first_name} {user.last_name}."
                data['email'] = user.email

            return Response(data)
        except User.DoesNotExist:
            return Response({"error": True, "message": f"Email {request.data['email']} with code {request.data['code']} are wrong!"})


@api_view(['POST', ])
@permission_classes([IsAuthenticated])
def upload_pic(request):
    """ Updates User profile picture

    Args:
        request: Request access to consume the API

    Return:
        JSON with status and message if success
    """
    import base64

    try:
        user = User.objects.get(email=request.data['email'])

        # First the pic will be saved in the path
        file_media = f"profile_pics/profile_{user.id}.png"
        file_path = f"{settings.MEDIA_ROOT}/{file_media}"
        picture = request.data['profile_pic']

        picture_bytes = picture.encode('utf-8')
        with open(file_path, 'wb') as file:
            decoded_image_data = base64.decodebytes(picture_bytes)
            file.write(decoded_image_data)

        request.data['profile_photo'] = f"{settings.MEDIA_URL}{file_media}"

        # Now is updated the User model
        serializer = UserSerializer(
            instance=user,
            data=request.data,
            partial=True  # To update some fields but not necessarily all at once.
        )
        data = {}
        if serializer.is_valid(raise_exception=True):
            user = serializer.upload_pic(user)
            data['success'] = f"Profile photo for user {user.first_name} {user.last_name} has been updated."
            data['email'] = user.email

        return Response(data)
    except User.DoesNotExist:
        return Response({"error": True, "message": f"{request.data['email']} user can't be updated because doesn't exists"})
