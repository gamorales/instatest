from rest_framework import serializers

from .models import User


class UserSerializer(serializers.ModelSerializer):

    confirm_password = serializers.CharField(
        style={'input_type': 'password'},
        write_only=True,
        min_length=8,
        max_length=20
    )

    class Meta:
        model = User
        fields = [
            'id', 'email', 'first_name', 'last_name', 'profile_photo',
            'date_joined', 'is_superuser', 'is_active', 
            'password', 'confirm_password'
        ]
        extra_kwargs = {
            'password': {'write_only': True}  # Security to avoid users can read the field
        }

    def validate_fields(self, email, password, confirm_password):
        """ Validates user data before saving

        Before update or create a new user via API, the fields that are sent, must be validated

        Args:
            email: This field must be the format [a-zA-Z0-9]@[a-zA-Z0-9].[a-zA-Z0-9]
            password: This field must have with 8-20 characters alphanumeric and extra character
            confirm_password: This field must be equal to password field

        Raises:
            serializers.ValidationError: An error ocurred when the email, the password or
                                         the confirm_password are wrong
        """
        import re

        regex_email = r"\"?([-a-zA-Z0-9.`?{}]+@\w+\.\w+)\"?"
        regex_password = '^(?=\S{6,20}$)(?=.*?\d)(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[^A-Za-z\s0-9])'

        # Fields Validation
        if not re.search(regex_email, email):
            raise serializers.ValidationError({"email": "Email is invalid."})

        if password != confirm_password:
            raise serializers.ValidationError({"password": "Passwords don't match."})

        if len(password) < 8:
            raise serializers.ValidationError({"password": "Password minimum 8 characters."})

        if not re.search(regex_password, password):
            raise serializers.ValidationError(
                {
                    "password": """
Password must contains numbers, uppercase and lowercase 
letters and at least one special character.
"""
                }
            )

    def save(self):
        """ Builds, validates and saves the User object

        Args:
            email: This field must be the format [a-zA-Z0-9]@[a-zA-Z0-9].[a-zA-Z0-9]
            password: This field must have with 6-20 characters alphanumeric and extra character
            confirm_password: This field must be equal to password field
            first_name:
            last_name:

        Return:
            Object of the user created

        Raises:
            serializers.ValidationError: An error ocurred when the first_name or last_name are empty
        """
        first_name = self.validated_data['first_name']
        last_name = self.validated_data['last_name']
        email = self.validated_data['email']
        password = self.validated_data['password']
        confirm_password = self.validated_data['confirm_password']

        # Validate fields first and last name won't be empty
        if first_name == '':
            raise serializers.ValidationError("First Name must not be empty")
        if last_name == '':
            raise serializers.ValidationError("Last Name must not be empty")

        self.validate_fields(email, password, confirm_password)

        user = User(
            email=email,
            first_name=first_name,
            last_name=last_name,
            password=password,
            is_active=True,
        )
        user.set_password(password)
        user.save()
        return user

    def update(self, instance):
        """ Builds, validates and updates the User object

        Args:
            instance: User object to be updated
            email: This field must be the format [a-zA-Z0-9]@[a-zA-Z0-9].[a-zA-Z0-9]
            password: This field must have with 8-20 characters alphanumeric and extra character
            confirm_password: This field must be equal to password field
            first_name:
            last_name:

        Return:
            Object of the user updated

        Raises:
            serializers.ValidationError: An error ocurred when the first_name or last_name are empty
            serializers.ValidationError: An error ocurred when all fields are empty
        """

        email = self.validated_data['email']
        first_name = self.validated_data['first_name']
        last_name = self.validated_data['last_name']
        password = self.validated_data['password']
        confirm_password = self.validated_data['confirm_password']

        if password != '' and password != '________':
            self.validate_fields(email, password, confirm_password)
            instance.set_password(password)

        # Validate fields won't be empty
        if email == '' and first_name == '' and last_name == '':
            raise serializers.ValidationError("You must send at least one field with data")

        if email != '':
            instance.email = email

        if first_name != '':
            instance.first_name = first_name

        if last_name != '':
            instance.last_name = last_name

        instance.save()
        return instance

    def disable(self, instance):
        """ Disables User object

        Args:
            instance: pk or id of the record to be updated

        Return:
            Object of the user updated
        """

        instance.is_active = False
        instance.save()
        return instance

    def code(self, instance):
        """ Generate random value for User code field

        The value will be a 6 random number between 100000 and 999999

        Args:
            instance: pk or id of the record to be updated

        Return:
            Object of the user updated
        """
        import random
        instance.code = random.randint(100000, 999999)
        instance.save()
        return instance

    def reset_password(self, instance):
        """ Save new password for User

        The value will be a 6 random number between 100000 and 999999

        Args:
            instance: pk or id of the record to be updated

        Return:
            Object of the user updated

        Raises:
            serializers.ValidationError: An error ocurred when the first_name or last_name are empty
            serializers.ValidationError: An error ocurred when all fields are empty
        """

        email = self.validated_data['email']
        password = self.validated_data['password']
        confirm_password = self.validated_data['confirm_password']

        if password != '':
            self.validate_fields(email, password, confirm_password)
            instance.set_password(password)
        
        instance.code = 0
        instance.save()
        return instance

    def upload_pic(self, instance):
        instance.profile_photo = self.validated_data['profile_photo']
        instance.save()
        return instance
