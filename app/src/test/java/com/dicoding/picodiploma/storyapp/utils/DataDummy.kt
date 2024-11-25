package com.dicoding.picodiploma.storyapp.utils

import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "Author $i",
                description = "Description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                createdAt = "2024-01-$i",
                lat = null,
                lon = null
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyUserModel(): UserModel {
        return UserModel(
            email = "test@test.com",
            token = "dummy_token",
            isLogin = true
        )
    }
}