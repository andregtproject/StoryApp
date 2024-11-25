package com.dicoding.picodiploma.storyapp.view

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.di.Injection
import com.dicoding.picodiploma.storyapp.view.detail.DetailViewModel
import com.dicoding.picodiploma.storyapp.view.login.LoginViewModel
import com.dicoding.picodiploma.storyapp.view.main.MainViewModel
import com.dicoding.picodiploma.storyapp.view.map.MapsViewModel
import com.dicoding.picodiploma.storyapp.view.signup.SignupViewModel
import com.dicoding.picodiploma.storyapp.view.upload.UploadStoryViewModel

class ViewModelFactory private constructor(
    private val repository: StoryRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, application) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }

            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                        context.applicationContext as Application
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}