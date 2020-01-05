package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class PasswordResponseDTO (
    @SerializedName("response") var response:Boolean?,
    @SerializedName("message") var message:String?
)