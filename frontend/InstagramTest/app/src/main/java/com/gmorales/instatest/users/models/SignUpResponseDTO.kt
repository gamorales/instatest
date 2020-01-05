package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class SignUpResponseDTO (
    @SerializedName("user") var customer:UserDTO?
)