package com.gmorales.instatest.users.models

import com.google.gson.annotations.SerializedName

data class UserDTO (
    @SerializedName("name") var name:String,
    @SerializedName("is_premium") var is_premium:Boolean,
    @SerializedName("code") var code:String,
    @SerializedName("_id") var id:String,
    @SerializedName("email") var email:String,
    @SerializedName("password") var password:String,
    @SerializedName("created_at") var created_at:String,
    @SerializedName("membership_due_date_at") var membership_due_date_at:String?
)