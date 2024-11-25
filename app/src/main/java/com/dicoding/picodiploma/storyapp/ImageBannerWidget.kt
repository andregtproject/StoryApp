package com.dicoding.picodiploma.storyapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.dicoding.picodiploma.storyapp.data.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ImagesBannerWidget : AppWidgetProvider() {
    companion object {
        const val EXTRA_ITEM = "com.dicoding.picodiploma.EXTRA_ITEM"
        const val UPDATE_WIDGET_ACTION = "com.dicoding.picodiploma.UPDATE_WIDGET"

        fun updateWidget(context: Context) {
            val intent = Intent(context, ImagesBannerWidget::class.java).apply {
                action = UPDATE_WIDGET_ACTION
            }
            context.sendBroadcast(intent)
        }

        @Suppress("DEPRECATION")
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.image_banner_widget)

            val pref = UserPreference.getInstance(context.dataStore)
            var isLoggedIn = false

            runBlocking {
                try {
                    val userModel = pref.getSession().first()
                    isLoggedIn = userModel.isLogin
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (isLoggedIn) {
                val intent = Intent(context, StackWidgetService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = toUri(Intent.URI_INTENT_SCHEME).toUri()
                }

                views.apply {
                    setRemoteAdapter(R.id.stack_view, intent)
                    setEmptyView(R.id.stack_view, R.id.empty_view)
                    setViewVisibility(R.id.stack_view, android.view.View.VISIBLE)
                    setViewVisibility(R.id.banner_text, android.view.View.VISIBLE)
                    setViewVisibility(R.id.empty_view, android.view.View.VISIBLE)
                    setTextViewText(
                        R.id.empty_view,
                        context.getString(R.string.no_stories_available)
                    )
                }

            } else {
                views.apply {
                    setViewVisibility(R.id.stack_view, android.view.View.GONE)
                    setViewVisibility(R.id.banner_text, android.view.View.GONE)
                    setViewVisibility(R.id.empty_view, android.view.View.VISIBLE)
                    setTextViewText(R.id.empty_view, context.getString(R.string.please_login))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UPDATE_WIDGET_ACTION) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, ImagesBannerWidget::class.java)
            )

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view)

            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}