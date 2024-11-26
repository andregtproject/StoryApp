package com.dicoding.picodiploma.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.local.entity.StoryEntity
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.utils.DataDummy
import com.dicoding.picodiploma.storyapp.utils.MainDispatcherRule
import com.dicoding.picodiploma.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var application: android.app.Application

    private lateinit var mainViewModel: MainViewModel
    private val dummyStories = DataDummy.generateDummyStoryResponse().map { story ->
        StoryEntity(
            id = story.id!!,
            name = story.name,
            description = story.description,
            photoUrl = story.photoUrl,
            createdAt = story.createdAt,
            lat = story.lat,
            lon = story.lon
        )
    }

    @Before
    fun setup() {
        Mockito.`when`(storyRepository.getSession())
            .thenReturn(flowOf(UserModel("test@email.com", "token", true)))
        Mockito.`when`(storyRepository.getStoriesPager())
            .thenReturn(flowOf(PagingData.from(dummyStories)))

        mainViewModel = MainViewModel(storyRepository, application)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data = PagingData.from(dummyStories)
        Mockito.`when`(storyRepository.getStoriesPager())
            .thenReturn(flowOf(data))

        val actualStories = mainViewModel.storyPagingList.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryEntityDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyData = PagingData.empty<StoryEntity>()
        Mockito.`when`(storyRepository.getStoriesPager())
            .thenReturn(flowOf(emptyData))

        val actualStories = mainViewModel.storyPagingList.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryEntityDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `getSession Should Return User Session`() {
        val expectedUser = UserModel("test@email.com", "token", true)
        Mockito.`when`(storyRepository.getSession())
            .thenReturn(flowOf(expectedUser))

        val actualUser = mainViewModel.getSession().getOrAwaitValue()

        assertEquals(expectedUser, actualUser)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    object StoryEntityDiffCallback : DiffUtil.ItemCallback<StoryEntity>() {
        override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}