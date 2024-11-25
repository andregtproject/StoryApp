package com.dicoding.picodiploma.storyapp.view.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.dicoding.picodiploma.storyapp.data.StoryRepository
import com.dicoding.picodiploma.storyapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.utils.DataDummy
import com.dicoding.picodiploma.storyapp.utils.LiveDataTestUtil
import com.dicoding.picodiploma.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.Mockito.`when`
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
    private lateinit var application: Application

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storyRepository, application)
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyData = PagingData.empty<ListStoryItem>()
        `when`(storyRepository.getStoriesPager()).thenReturn(flowOf(emptyData))

        val actualStory = mainViewModel.storyPagingList
        assertNotNull(actualStory)

        Mockito.verify(storyRepository).getStoriesPager()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyListStoryItem()
        val data = PagingData.from(dummyStories)
        `when`(storyRepository.getStoriesPager()).thenReturn(flowOf(data))

        val actualStory = mainViewModel.storyPagingList

        assertNotNull(actualStory)
        Mockito.verify(storyRepository).getStoriesPager()
    }

    @Test
    fun `when Get Session Should Return User Model`() = runTest {
        val expectedUser = DataDummy.generateDummyUserModel()
        val userFlow = flowOf(expectedUser)
        `when`(storyRepository.getSession()).thenReturn(userFlow)

        val actualUser = mainViewModel.getSession()

        assertNotNull(actualUser)
        val userValue = LiveDataTestUtil.getValue(actualUser)
        assertEquals(expectedUser.token, userValue.token)
        assertEquals(expectedUser.email, userValue.email)
        assertEquals(expectedUser.isLogin, userValue.isLogin)
    }

    @Test
    fun `verify Logout Calls Repository Logout`() = runTest {
        mainViewModel.logout()

        Mockito.verify(storyRepository).logout()
    }
}