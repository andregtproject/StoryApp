package com.dicoding.picodiploma.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storyDetail = MutableLiveData<ResultState<Story>>()
    val storyDetail: LiveData<ResultState<Story>> = _storyDetail

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            try {
                _storyDetail.value = ResultState.Loading
                _storyDetail.value = repository.getDetailStory(id)
            } catch (e: Exception) {
                _storyDetail.value = ResultState.Error(e.message ?: "An error occurred")
            }
        }
    }
}