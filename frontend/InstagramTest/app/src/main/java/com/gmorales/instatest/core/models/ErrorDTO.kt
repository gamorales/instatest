package com.gmorales.instatest.core.models

import com.google.gson.annotations.SerializedName

data class ErrorDTO (
    @SerializedName("detail") var detail:String?,
    @SerializedName("email") var email:String?,
    @SerializedName("password") var password:String?
)
