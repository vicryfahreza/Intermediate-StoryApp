package com.vicryfahreza.storyapp.service

import com.vicryfahreza.storyapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("stories")
    fun uploadStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<AddStoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginWithToken(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<Login>

    @FormUrlEncoded
    @POST("register")
    fun registerAccount(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ) : Call<RegisterResponse>

    @GET("stories")
    fun getTokenForMap(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Call<AllStoryResponse>

    @GET("stories?location=1")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoryResponse

}