package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class PasswordResponseDTO (
    @SerializedName("success") var success:String?,
    @SerializedName("email") var email:String?,
    @SerializedName("code") var code:Int?,
    @SerializedName("error") var error:Boolean?,
    @SerializedName("message") var message:String?
)