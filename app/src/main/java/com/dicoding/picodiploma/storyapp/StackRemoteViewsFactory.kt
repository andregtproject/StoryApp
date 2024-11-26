package com.dicoding.picodiploma.storyapp

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.api.ApiConfig
import com.dicoding.picodiploma.storyapp.data.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.pref.dataStore
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.view.detail.DetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = ArrayList<ListStoryItem>()
    private val pref by lazy { UserPreference.getInstance(mContext.dataStore) }
    private val repository: StoryRepository by lazy {
        val apiService = ApiConfig.getApiService("")
        StoryRepository.getInstance(apiService, pref, this.mContext)
    }

    override fun onCreate() {
        onDataSetChanged()
    }

    override fun onDataSetChanged() {
        mWidgetItems.clear()

        runBlocking(Dispatchers.IO) {
            try {
                val userModel = pref.getSession().first()

                if (userModel.isLogin) {
                    repository.updateApiService(userModel.token)

                    val stories = repository.getStoriesForWidget()
                    mWidgetItems.addAll(stories)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)

        if (position < mWidgetItems.size) {
            try {
                val story = mWidgetItems[position]
                val bitmap = try {
                    Glide.with(mContext.applicationContext)
                        .asBitmap()
                        .load(story.photoUrl)
                        .submit()
                        .get(5, TimeUnit.SECONDS)
                } catch (e: Exception) {
                    null
                }

                rv.apply {
                    if (bitmap != null) {
                        setImageViewBitmap(R.id.imageView, bitmap)
                    } else {
                        setImageViewResource(R.id.imageView, R.drawable.ic_image_placeholder)
                    }
                    setTextViewText(R.id.story_name, story.name ?: "")
                }

                val extras = bundleOf(
                    DetailActivity.EXTRA_STORY_ID to story.id,
                    ImagesBannerWidget.EXTRA_ITEM to position
                )
                val fillInIntent = Intent().apply {
                    putExtras(extras)
                }
                rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

            } catch (e: Exception) {
                e.printStackTrace()
                setDefaultView(rv)
            }
        } else {
            setDefaultView(rv)
        }

        return rv
    }

    private fun setDefaultView(rv: RemoteViews) {
        rv.apply {
            setImageViewResource(R.id.imageView, R.drawable.ic_image_placeholder)
            setTextViewText(R.id.story_name, "")
        }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}