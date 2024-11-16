package com.dicoding.picodiploma.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.register(name, email, password)
                _registerResult.value = result
            } catch (e: Exception) {
                _registerResult.value = ResultState.Error(e.message ?: "Registration failed")
            }
        }
    }
}