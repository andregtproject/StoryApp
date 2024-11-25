package com.dicoding.picodiploma.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data[0] = value
                latch.countDown()
                liveData.removeObserver(this)
            }
        }

        liveData.observeForever(observer)

        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            liveData.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}