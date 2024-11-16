package com.dicoding.picodiploma.storyapp.data.api

import com.dicoding.picodiploma.storyapp.data.response.DetailResponse
import com.dicoding.picodiploma.storyapp.data.response.LoginResponse
import com.dicoding.picodiploma.storyapp.data.response.RegisterResponse
import com.dicoding.picodiploma.storyapp.data.response.StoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailResponse
}