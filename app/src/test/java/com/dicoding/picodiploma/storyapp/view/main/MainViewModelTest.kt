package com.dicoding.picodiploma.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.utils.DataDummy
import com.dicoding.picodiploma.storyapp.utils.MainDispatcherRule
import com.dicoding.picodiploma.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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

    private lateinit var mainViewModel: MainViewModel
    private val dummyStories = DataDummy.generateDummyStoryResponse()

    @Before
    fun setup() {
        val application = Mockito.mock(android.app.Application::class.java)
        mainViewModel = MainViewModel(storyRepository, application)
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyData = PagingData.empty<ListStoryItem>()

        Mockito.`when`(storyRepository.getSession()).thenReturn(flowOf(UserModel("test@email.com", "token", true)))
        Mockito.`when`(storyRepository.getStoriesPager()).thenReturn(flowOf(emptyData))

        // Collect the first value from the Flow
        val actualStories = mainViewModel.storyPagingList.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryItemDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `when Get Story Not Null and Return Data`() = runTest {
        val data = PagingData.from(dummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        Mockito.`when`(storyRepository.getSession()).thenReturn(flowOf(UserModel("test@email.com", "token", true)))
        Mockito.`when`(storyRepository.getStoriesPager()).thenReturn(flowOf(data))

        val actualStories = mainViewModel.storyPagingList.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryItemDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    object StoryItemDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}