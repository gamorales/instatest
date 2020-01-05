package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class PasswordResetResponseDTO (
    @SerializedName("success") var success:String?,
    @SerializedName("email") var email:String?,
    @SerializedName("error") var error:Boolean?,
    @SerializedName("message") var message:String?
)