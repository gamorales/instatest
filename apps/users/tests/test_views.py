import json

from django.test import TestCase, Client
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient
from rest_framework_simplejwt.settings import api_settings

from apps.users.models import User
from apps.users.serializers import UserSerializer


# initialize the APIClient app
client = Client()
api_client = APIClient()


class TestViews(TestCase):

    def setUp(self):
        self.user1 = User.objects.create(
            email="test@dealeros.io",
            first_name="User",
            last_name="Test",
            password="testpassword",
            is_active=True
        )
        self.user1.save()

        self.user2 = User.objects.create_superuser(
            email="test2@dealeros.io",
            password="testpassword",
            is_active=True,
            is_staff=True,
            is_superuser=True
        )
        self.user2.set_password("testpassword")
        self.user2.save()

    def test_get_all_users(self):
        response = client.get(reverse('users:users-list'))
        users = User.objects.all()

        serializer = UserSerializer(users, many=True)
        self.assertEqual(response.data, serializer.data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_get_single_user(self):
        resp = api_client.post(
            reverse('token_obtain_pair'),
            {
                'email': 'info@infos.com',
                'password': 'ArPg44628/*GoKu*/'
            },
            format='json'
        )
        print(resp.data)
        if resp.status_code == status.HTTP_200_OK:
            self.credentials(
                HTTP_AUTHORIZATION="{0} {1}".format(api_settings.JWT_AUTH_HEADER_PREFIX, resp.data['token']))

            print(resp.data)
        else:
            print("Nada")

        # print(resp.data)
        # # self.assertEqual(resp.status_code, status.HTTP_401_UNAUTHORIZED)
        # # self.assertTrue('token' in resp.data)
        #
        # response = client.get(reverse('users:single-user', kwargs={'pk': self.user1.id}))
        # user = User.objects.get(pk=self.user1.id)
        #
        # serializer = UserSerializer(user)
        # self.assertEqual(response.data, serializer.data)
        # self.assertEqual(response.status_code, status.HTTP_200_OK)
