package com.gmorales.instatest.users.controllers

import com.gmorales.instatest.users.models.TokenDTO
import com.gmorales.instatest.users.models.LoginResponseDTO
import com.gmorales.instatest.users.models.PasswordResponseDTO
import com.gmorales.instatest.users.models.SignUpResponseDTO

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
    @POST("customers/signup")
    fun signupAuth(
        @Field("email") email:String,
        @Field("password") password:String
    ):Call<SignUpResponseDTO>

    @FormUrlEncoded
    @POST("customers/forgot-password")
    fun forgotPassword(
        @Field("email") email:String
    ):Call<PasswordResponseDTO>

    @FormUrlEncoded
    @POST("customers/check-code")
    fun checkCode(
        @Field("email") email:String,
        @Field("code") code:String
    ):Call<PasswordResponseDTO>

    @FormUrlEncoded
    @POST("customers/reset-password")
    fun resetPassword(
        @Field("email") email:String,
        @Field("code") code:String,
        @Field("password") password:String
    ):Call<SignUpResponseDTO>
}

