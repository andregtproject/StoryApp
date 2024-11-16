package com.dicoding.picodiploma.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                if (result is ResultState.Success) {
                    val userModel = result.data.loginResult?.token?.let {
                        UserModel(
                            email = email,
                            token = it,
                            isLogin = true
                        )
                    }
                    if (userModel != null) {
                        saveSession(userModel)
                    }
                }
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = ResultState.Error(e.message ?: "Login failed")
            }
        }
    }

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}