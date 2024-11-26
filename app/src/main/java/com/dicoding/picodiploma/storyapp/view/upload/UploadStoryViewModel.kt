package com.dicoding.picodiploma.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.response.UploadResponse
import java.io.File

class UploadStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(
        imageFile: File,
        description: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): LiveData<ResultState<UploadResponse>?> {
        return repository.uploadImage(imageFile, description, latitude, longitude)
    }
}
