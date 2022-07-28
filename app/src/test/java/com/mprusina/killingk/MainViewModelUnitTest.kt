package com.mprusina.killingk

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mprusina.killingk.data.ProfileRepository
import com.mprusina.killingk.data.api.ProfileService
import com.mprusina.killingk.data.entities.ProfileResponse
import com.mprusina.killingk.data.entities.ProfileResponseData
import com.mprusina.killingk.ui.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class MainViewModelUnitTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var profileService: ProfileService

    private lateinit var repository: ProfileRepository

    private lateinit var viewModel: MainViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(mainThreadSurrogate)
        repository = ProfileRepository(profileService)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun when_profileNotCompleted_then_isProfileCompleted_returnFalse() {
        runBlocking {
            val response = ProfileResponse(false, ProfileResponseData("title", "message"))
            `when`(repository.completeProfile()).thenReturn(response)
            viewModel.completeProfile()
            delay(3000) // delay added as there there is delay in viewModel.completeProfile(). If not added, tests will fail.
        }
        assertEquals(false, viewModel.isProfileCompleted())
    }

    @Test
    fun when_ProfileCompleted_then_isProfileCompleted_returnTrue() {
        runBlocking {
            val response = ProfileResponse(true, ProfileResponseData("title", "message"))
            `when`(repository.completeProfile()).thenReturn(response)
            viewModel.completeProfile()
            delay(3000)
        }
        assertEquals(true, viewModel.isProfileCompleted())
    }

    @Test
    fun when_ProfileCompleted_then_ProfileResponse_notNull() {
        runBlocking {
            val response = ProfileResponse(true, ProfileResponseData("title", "message"))
            `when`(repository.completeProfile()).thenReturn(response)
            viewModel.completeProfile()
            delay(3000)
        }
        assertNotNull(viewModel.getProfileResponse().getOrAwaitValue())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}

private fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val countdown = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            countdown.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    if (!countdown.await(time, timeUnit)) {
        throw TimeoutException("LiveData value not set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}