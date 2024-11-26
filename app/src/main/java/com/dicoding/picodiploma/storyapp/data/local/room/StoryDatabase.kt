package com.dicoding.picodiploma.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.storyapp.data.local.dao.RemoteKeysDao
import com.dicoding.picodiploma.storyapp.data.local.dao.StoryDao
import com.dicoding.picodiploma.storyapp.data.local.entity.RemoteKeys
import com.dicoding.picodiploma.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 3,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): StoryDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                StoryDatabase::class.java,
                "story_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}