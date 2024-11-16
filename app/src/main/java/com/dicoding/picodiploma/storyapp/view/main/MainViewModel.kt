package com.dicoding.picodiploma.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storyList = MutableLiveData<ResultState<List<ListStoryItem>>>()
    val storyList: LiveData<ResultState<List<ListStoryItem>>> = _storyList

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories() {
        viewModelScope.launch {
            try {
                _storyList.value = repository.getStories()
            } catch (e: Exception) {
                _storyList.value = ResultState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}