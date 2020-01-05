package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class TokenDTO (
    @SerializedName("access") var access:String,
    @SerializedName("refresh") var refresh:String
)
