package com.dicoding.picodiploma.storyapp.utils

import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i",
                createdAt = "2024-01-$i",
                lat = -6.8957643 + i,
                lon = 107.6338462 + i
            )
            items.add(story)
        }
        return items
    }
}