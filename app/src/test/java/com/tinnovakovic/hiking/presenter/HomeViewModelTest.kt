package com.tinnovakovic.hiking.presenter


import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.HikingPhotoRepository
import com.tinnovakovic.hiking.domain.location.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.location.StopLocationServiceUseCase
import com.tinnovakovic.hiking.presentation.HomeContract
import com.tinnovakovic.hiking.presentation.HomeViewModel
import com.tinnovakovic.hiking.shared.ContextProvider
import com.tinnovakovic.hiking.shared.CoroutineTestExtension
import com.tinnovakovic.hiking.shared.ErrorToUser
import com.tinnovakovic.hiking.shared.ExceptionHandler
import com.tinnovakovic.hiking.shared.network.ConnectivityObserver
import com.tinnovakovic.hiking.shared.permission.PermissionProvider
import io.mockk.coEvery
import io.mockk.mockk
import com.tinnovakovic.hiking.R
import io.mockk.Ordering
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val locationInMemoryCache: LocationInMemoryCache = mockk(relaxed = true)
    private val hikingPhotoRepo: HikingPhotoRepository = mockk(relaxed = true)
    private val connectivityObserver: ConnectivityObserver = mockk(relaxed = true)
    private val permissionProvider: PermissionProvider = mockk(relaxed = true)
    private val exceptionHandler: ExceptionHandler = mockk(relaxed = true)
    private val contextProvider: ContextProvider = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)

    private lateinit var sut: HomeViewModel

    private fun createSut() {
        sut = HomeViewModel(
            startLocationServiceUseCase,
            stopLocationServiceUseCase,
            locationInMemoryCache,
            hikingPhotoRepo,
            connectivityObserver,
            permissionProvider,
            exceptionHandler,
            contextProvider,
            savedStateHandle
        )
    }

    @Test // Simulates device is offline when opening the app
    fun `GIVEN observeIsOnline() is false, WHEN initialise(), THEN verify stopLocationService is called & assert UiState isStartStopButtonEnabled=true, errorMessage=LocationError()`() =
        runTest {
            //GIVEN
            coEvery { savedStateHandle.get<Boolean>(HomeViewModel.SAVED_STATE_IS_START) } returns null
            coEvery { connectivityObserver.observeIsOnline() } returns MutableStateFlow(false).asStateFlow()
            coEvery { contextProvider.getContext().getString(R.string.offline_message) } returns "offline"
            coEvery { hikingPhotoRepo.getHikingPhotosStream() } returns flowOf(emptyList())
            createSut()

            sut.uiState.test {
                //WHEN
                sut.onUiEvent(HomeContract.UiEvents.Initialise)

                awaitItem() //ignore the initial hardcoded state

                //THEN
                val firstUiState = awaitItem()
                assertFalse(firstUiState.isStartStopButtonEnabled)
                assertTrue(firstUiState.errorMessage == ErrorToUser.LocationError("offline"))
                assertTrue(firstUiState.hikingPhotos.isEmpty())
                assertTrue(firstUiState.isStartButton)
                assertFalse(firstUiState.isObservingLocation)
            }

            verify(exactly = 1) {
                stopLocationServiceUseCase.execute()
            }
        }

    @Test // Simulates device is offline when opening the app, then the device goes online
    fun `GIVEN observeIsOnline() emits false then true, WHEN initialise(), THEN verify stopLocationService is called first, then startLocationService is called second`() =
        runTest {
            //GIVEN
            val networkState = MutableStateFlow(false)
            coEvery { savedStateHandle.get<Boolean>(HomeViewModel.SAVED_STATE_IS_START) } returns null
            coEvery { connectivityObserver.observeIsOnline() } returns networkState
            coEvery { contextProvider.getContext().getString(R.string.offline_message) } returns "offline"
            createSut()

            sut.uiState.test {
                //WHEN
                sut.onUiEvent(HomeContract.UiEvents.Initialise)

                awaitItem() //ignore the initial hardcoded state

                //THEN Assert Second UiState where observeIsOffline = false
                val firstUiState = awaitItem()
                assertFalse(firstUiState.isStartStopButtonEnabled)
                assertTrue(firstUiState.errorMessage == ErrorToUser.LocationError("offline"))

                //WHEN Simulate going online
                networkState.value = true

                //THEN Assert Third UiState
                val secondUiState = awaitItem()
                assertTrue(secondUiState.isStartStopButtonEnabled)
                assertTrue(secondUiState.errorMessage == null)
                assertTrue(secondUiState.hikingPhotos.isEmpty())
                assertTrue(secondUiState.isStartButton)
                assertFalse(secondUiState.isObservingLocation)
            }

            verify(ordering = Ordering.SEQUENCE) {
                stopLocationServiceUseCase.execute()
                startLocationServiceUseCase.execute()
            }
        }
}
