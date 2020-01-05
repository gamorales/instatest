package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class Errors (
    @SerializedName("value") var value:String?,
    @SerializedName("msg") var msg:String?,
    @SerializedName("param") var param:String?,
    @SerializedName("location") var location:String?
)