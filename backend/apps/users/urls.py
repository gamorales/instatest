from django.urls import path

from apps.users.views import (
    registration, update, delete, disable,
    get_user, get_users_list, user_data
)

app_name = 'users'

urlpatterns = [
    path('login/', user_data, name='login'),
    path('register/', registration, name='register'),
    path('update/<int:pk>', update, name='update'),
    path('delete/<int:pk>', delete, name='delete'),
    path('disable/<int:pk>', disable, name='disable'),
    path('list/<int:pk>', get_user, name='single-user'),
    path('list/', get_users_list, name='users-list'),

]
