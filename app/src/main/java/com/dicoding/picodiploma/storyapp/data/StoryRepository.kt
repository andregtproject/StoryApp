package com.dicoding.picodiploma.storyapp.data

import com.dicoding.picodiploma.storyapp.data.api.ApiService
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.response.ErrorResponse
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.data.response.LoginResponse
import com.dicoding.picodiploma.storyapp.data.response.RegisterResponse
import com.dicoding.picodiploma.storyapp.data.response.Story
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun register(name: String, email: String, password: String): ResultState<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            ResultState.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            ResultState.Error(errorBody.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun login(email: String, password: String): ResultState<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            ResultState.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            ResultState.Error(errorBody.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun getStories(): ResultState<List<ListStoryItem>> {
        return try {
            val response = apiService.getStories()
            ResultState.Success(response.listStory)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            ResultState.Error(errorBody.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getDetailStory(id: String): ResultState<Story> {
        return try {
            val response = apiService.getDetailStory(id)
            ResultState.Success(response.story!!)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            ResultState.Error(errorBody.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}