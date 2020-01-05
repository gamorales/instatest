package com.gmorales.instatest.users.controllers

import com.gmorales.instatest.users.models.*

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {

    @FormUrlEncoded
    @POST("login/")
    fun loginAuth(
        @Field("email") email:String,
        @Field("password") password:String
    ):Call<TokenDTO>

    @FormUrlEncoded
    @POST("users/login/")
    fun userDataAuth(
        @Header("Authorization") token:String,
        @Field("email") email:String
    ):Call<LoginResponseDTO>

    @FormUrlEncoded
    @POST("users/register/")
    fun signupAuth(
        @Field("first_name") first_name:String,
        @Field("last_name") last_name:String,
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("confirm_password") confirm_password:String
    ):Call<SignUpResponseDTO>

    @FormUrlEncoded
    @POST("users/forgot-password/")
    fun forgotPassword(
        @Field("email") email:String
    ):Call<PasswordResponseDTO>

    @FormUrlEncoded
    @POST("users/reset-password/")
    fun resetPassword(
        @Field("email") email:String,
        @Field("code") code:Int,
        @Field("password") password:String,
        @Field("confirm_password") confirm_password:String
    ):Call<PasswordResetResponseDTO>
}
