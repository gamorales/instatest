package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class LoginResponseDTO (
    @SerializedName("id") var id:Int,
    @SerializedName("email") var email:String,
    @SerializedName("first_name") var first_name:String,
    @SerializedName("last_name") var last_name:String,
    @SerializedName("profile_photo") var profile_photo:String,
    @SerializedName("date_joined") var date_joined:String,
    @SerializedName("is_superuser") var superuser:Boolean,
    @SerializedName("is_active") var active:Boolean
)
