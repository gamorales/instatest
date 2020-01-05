package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class SignUpResponseDTO (
    @SerializedName("success") var success:String?,
    @SerializedName("name") var name:String?,
    @SerializedName("email") var email:String?,
    @SerializedName("first_name") var first_name:String?,
    @SerializedName("last_name") var last_name:String?,
    @SerializedName("password") var password:String?,
    @SerializedName("confirm_password") var confirm_password:String?
)