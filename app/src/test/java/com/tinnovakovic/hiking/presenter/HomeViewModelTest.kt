package com.tinnovakovic.hiking.presenter

import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import com.tinnovakovic.hiking.data.LocationInMemoryCache
import com.tinnovakovic.hiking.domain.GetPhotoFromLocationUseCase
import com.tinnovakovic.hiking.domain.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.StopLocationServiceUseCase
import com.tinnovakovic.hiking.presentation.HomeContract
import com.tinnovakovic.hiking.presentation.HomeViewModel
import com.tinnovakovic.hiking.shared.CoroutineTestExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @RegisterExtension
    @JvmField
    val coroutineTestExtension = CoroutineTestExtension(testDispatcher)

    private val startLocationServiceUseCase: StartLocationServiceUseCase = mockk(relaxed = true)
    private val stopLocationServiceUseCase: StopLocationServiceUseCase = mockk(relaxed = true)
    private val photoFromLocationUseCase: GetPhotoFromLocationUseCase = mockk(relaxed = true)
    private val locationInMemoryCache: LocationInMemoryCache = mockk(relaxed = true)

    private lateinit var sut: HomeViewModel

    private fun createSut() {
        sut = HomeViewModel(
            startLocationServiceUseCase,
            stopLocationServiceUseCase,
            photoFromLocationUseCase,
            locationInMemoryCache
        )
    }

    @Test
    fun `WHEN OnPause, THEN update uiState to scrollStateToTop=false`() = runTest {
        //GIVEN
        createSut()
        //WHEN
        sut.onUiEvent(HomeContract.UiEvents.OnPause)

        //THEN
        assertEquals(
            true,
            sut.uiState.value.isStartButton
        )
        assertEquals(
            false,
            sut.uiState.value.scrollStateToTop
        )
        assertEquals(
            false,
            sut.uiState.value.isError
        )
        assertEquals(
            0,
            sut.uiState.value.hikingPhotos.size
        )
    }
}
