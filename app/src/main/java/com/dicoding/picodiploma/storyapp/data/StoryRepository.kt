package com.dicoding.picodiploma.storyapp.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.dicoding.picodiploma.storyapp.data.api.ApiConfig
import com.dicoding.picodiploma.storyapp.data.api.ApiService
import com.dicoding.picodiploma.storyapp.data.local.StoryDatabase
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.remote.StoryRemoteMediator
import com.dicoding.picodiploma.storyapp.data.response.ErrorResponse
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.data.response.LoginResponse
import com.dicoding.picodiploma.storyapp.data.response.RegisterResponse
import com.dicoding.picodiploma.storyapp.data.response.Story
import com.dicoding.picodiploma.storyapp.data.response.StoryResponse
import com.dicoding.picodiploma.storyapp.data.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference,
    private val context: Context
) {
    private val database by lazy { StoryDatabase.getInstance(context) }

    fun updateApiService(token: String) {
        apiService = ApiConfig.getApiService(token)
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultState<RegisterResponse> {
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

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesPager() = Pager(
        config = PagingConfig(
            pageSize = 5
        ),
        remoteMediator = StoryRemoteMediator(
            database = database,
            apiService = apiService
        ),
        pagingSourceFactory = {
            database.storyDao().getAllStories()
        }
    ).flow

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

    fun uploadImage(
        imageFile: File,
        description: String,
        latitude: Double? = null,
        longitude: Double? = null
    ) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val latPart = latitude?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonPart = longitude?.toString()?.toRequestBody("text/plain".toMediaType())

        try {
            val successResponse = apiService.uploadImage(
                multipartBody,
                requestBody,
                latPart,
                lonPart
            )
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }
    }

    suspend fun getStoriesForWidget(): List<ListStoryItem> {
        return try {
            val user = userPreference.getSession().first()
            updateApiService(user.token)
            val response = apiService.getStories()
            response.listStory
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getStoriesWithLocation(location: Int = 1): LiveData<ResultState<StoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStoriesWithLocation(location)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "An unknown error occurred"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unknown error occurred"))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            context: Context
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference, context)
            }.also { instance = it }
    }
}