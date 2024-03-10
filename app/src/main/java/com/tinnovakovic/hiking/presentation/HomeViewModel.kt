package com.tinnovakovic.hiking.presentation

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.HikingPhotoRepository
import com.tinnovakovic.hiking.domain.location.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.location.StopLocationServiceUseCase
import com.tinnovakovic.hiking.shared.ContextProvider
import com.tinnovakovic.hiking.shared.ExceptionHandler
import com.tinnovakovic.hiking.shared.network.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startLocationServiceUseCase: StartLocationServiceUseCase,
    private val stopLocationServiceUseCase: StopLocationServiceUseCase,
    private val locationInMemoryCache: LocationInMemoryCache,
    private val hikingPhotoRepository: HikingPhotoRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val exceptionHandler: ExceptionHandler,
    private val contextProvider: ContextProvider,
    private val savedStateHandle: SavedStateHandle
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())

    private var initializeCalled = false

    private val coExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = exceptionHandler.execute(throwable)
        updateUiState {
            it.copy(errorMessage = errorMessage)
        }
    }

    @MainThread
    private fun initialise() {
        if (initializeCalled) return
        initializeCalled = true

        viewModelScope.launch {
            if (connectivityObserver.isOnline()) {
                Log.d(javaClass.name, "TINTIN, initialise() onlineState()")
                onlineState()
            } else {
                Log.d(javaClass.name, "TINTIN, initialise() offlineState()")
                offlineState()
            }
            restoreSavedState()
        }
        observeNetwork()
        observeRoomHikingPhotos()
    }

    private fun restoreSavedState() {
        viewModelScope.launch {
            savedStateHandle.get<Boolean>(SAVED_STATE_IS_START)?.let { isStart ->

                if (!isStart) observeLocationAndFetchPhotos()

                updateUiState {
                    it.copy(isStartButton = isStart)
                }
            }
        }
    }

    override fun onUiEvent(event: HomeContract.UiEvents) {
        when (event) {
            is HomeContract.UiEvents.Initialise -> initialise()

            is HomeContract.UiEvents.StartClicked -> {
                observeLocationAndFetchPhotos()
                startLocationServiceUseCase.execute()
                savedStateHandle.set(SAVED_STATE_IS_START, false)
                updateUiState { it.copy(isStartButton = false) }
            }

            is HomeContract.UiEvents.StopClicked -> {
                stopLocationServiceUseCase.execute()
                savedStateHandle.set(SAVED_STATE_IS_START, true)
                updateUiState {
                    it.copy(
                        isStartButton = true,
                    )
                }
            }

            is HomeContract.UiEvents.ResetClicked -> {
                viewModelScope.launch {
                    hikingPhotoRepository.clearDatabase()
                }
            }

            is HomeContract.UiEvents.OnDestroy -> stopLocationServiceUseCase.execute()
            is HomeContract.UiEvents.OnResume -> {
                updateUiState { it.copy(scrollStateToTop = true) }
            }

            is HomeContract.UiEvents.OnPause -> {
                updateUiState { it.copy(scrollStateToTop = false) }
            }

            HomeContract.UiEvents.PostScrollToTop -> {
                updateUiState { it.copy(scrollStateToTop = false) }
            }
        }
    }

    private fun observeLocationAndFetchPhotos() {
        if (!uiState.value.isObservingLocation) {
            Log.d(javaClass.name, "TINTIN observeLocationAndFetchPhotos()")
            updateUiState { it.copy(isObservingLocation = true) }
            locationInMemoryCache.cache.onEach { latestLocation ->
                latestLocation?.let { location ->
                    hikingPhotoRepository.fetchAndInsertPhoto(location)
                }
            }.launchIn(viewModelScope + coExceptionHandler)
        }
    }

    private fun observeRoomHikingPhotos() {
        Log.d(javaClass.name, "observeRoomHikingPhotos()")
        hikingPhotoRepository
            .getHikingPhotosStream()
            .onEach { hikingPhotos -> updateUiState { it.copy(hikingPhotos = hikingPhotos) } }
            .launchIn(viewModelScope + coExceptionHandler)
    }

    private fun observeNetwork() {
        connectivityObserver
            .observerIsOnline()
            .onEach { isOnline ->
                Log.d(javaClass.name, "TINTIN observeNetwork(), is online: $isOnline")
                if (isOnline) onlineState() else offlineState()
            }
            .launchIn(viewModelScope)
    }

    private fun onlineState() {
        Log.d(javaClass.name, "TINTIN, onlineState()")
        startLocationServiceUseCase.execute()
        updateUiState {
            it.copy(
                isStartStopButtonEnabled = true,
                errorMessage = null,
            )
        }
    }

    private fun offlineState() {
        Log.d(javaClass.name, "TINTIN, offlineState()")
        stopLocationServiceUseCase.execute()
        updateUiState {
            it.copy(
                isStartStopButtonEnabled = false,
                errorMessage = contextProvider.getContext()
                    .getString(R.string.offline_message),
            )
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true,
            hikingPhotos = listOf(),
            scrollStateToTop = false,
            errorMessage = null,
            isStartStopButtonEnabled = true,
            isObservingLocation = false
        )

        const val SAVED_STATE_IS_START = "saved_state_is_start"
    }
}

//TODO:
// - Observe state of notification and location permission and redisplay it if permission is turned off
// - Is LocationInMemoryCache the right term for what it is? LocationStateFlowProvider and StateFlowProvider
// - Add Error handling, check all error FlickrApi can send and exponential backoff, see android offline documentation
// - Check if compose is recomposing a lot, considering using a key with the LazyColumn
// Error Handling Ideas
//    - First retry an error, if it fails after three total attempt
//      display and error for a few seconds and carry on
// - Take screenshots, gifs and add to ReadMe
// - What errors do we need to handle from Location?
// - Inject Dispatchers for testing purposes

//TODO Optional:
// - Display button that scrolls to top when a new photo is added and lazyColumn is not on first item
// - Display dialog when user clicks reset asking user if they are certain
// - All IOException not being caught as one in ExceptionHandlerImpl
// - Display reset button only where there is data to delete (optional)

//TODO: No Internet Ideas
// - If there's no internet we should stop fetching a user location and
//   photos until their internet is back
//   - Pressing start and stop should become disabled until back online
//     and when back online continue from the state the user was last on
//      - SystemDeath stores the state of the button already for us
// - PHASE 2 - We can continue to track their location and fetch all
//   the photos they should have received at the time when back online

//DONE:
// - Save photos in Room
// - Have a savedStateHandle save a boolean
//   -- After process death recovery it should be true, then display the images and start location tracking again
// - Should database return a list of HikingPhotoEntity or HikingPhoto? Check offline app documentation
// - Observe internet state on initialise
// - Add button to reset photos/hike
// - Use our ApplicationScope in the Location tracking
// - iO, Http and FlickrApi errorExceptionHandling done
// - BUG: When offline and process death occurs we don't check the network state
// - BUG: Fetching two images at a time -> When State is STOP and internet state changes it fetches data
// - Not all errors need to be displayed, many can be silent errors that print to the log only
// - Improve error handling infinite loop

//TODO: Manual Test Instructions
// - Standard version
//   - Press start/stop
//   - Press reset
//   - Don't keep activity
//   - Terminate app and reopen
// - Offline
//   - Press start/stop
//   - Press reset
//   - Don't keep activity
//   - Terminate app and reopen
// - API Errors
//   - Test the standard API errors that are available

