package com.dicoding.picodiploma.storyapp.view.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyapp.ImagesBannerWidget
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    private val repository: StoryRepository,
    private val application: Application
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                if (user.isLogin) {
                    repository.updateApiService(user.token)
                }
            }
        }
    }

    val storyPagingList by lazy { repository.getStoriesPager().cachedIn(viewModelScope) }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            ImagesBannerWidget.updateWidget(application)
        }
    }
}