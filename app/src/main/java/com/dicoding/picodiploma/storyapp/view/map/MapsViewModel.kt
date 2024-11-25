package com.dicoding.picodiploma.storyapp.view.map

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.data.StoryRepository

class MapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation(location = 1)
}