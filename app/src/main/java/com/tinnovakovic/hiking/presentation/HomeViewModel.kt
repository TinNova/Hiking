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
import com.tinnovakovic.hiking.shared.network.NetworkStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startLocationServiceUseCase: StartLocationServiceUseCase,
    private val stopLocationServiceUseCase: StopLocationServiceUseCase,
    private val locationInMemoryCache: LocationInMemoryCache,
    private val hikingPhotoRepository: HikingPhotoRepository,
    private val networkStateProvider: NetworkStateProvider,
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
            if (networkStateProvider.isNetworkStateActive()) {
                Log.d(javaClass.name, "TINTIN, initialise() onlineState()")
                onlineState()
            } else {
                Log.d(javaClass.name, "TINTIN, initialise() offlineState()")
                offlineState()
            }
            restoreSavedState()
        }
        observeNetwork()
    }

    private fun restoreSavedState() {
        viewModelScope.launch {
            savedStateHandle.get<Boolean>(SAVED_STATE_IS_START)?.let { isStart ->

                updateUiState {
                    it.copy(isStartButton = isStart)
                }

                observeHikingPhotos() //observes database, doesn't fetch

            }
        }
    }

    override fun onUiEvent(event: HomeContract.UiEvents) {
        when (event) {
            is HomeContract.UiEvents.Initialise -> initialise()

            is HomeContract.UiEvents.StartClicked -> {
                observeLocationAndFetchPhotos()
                observeHikingPhotos()
                savedStateHandle.set(SAVED_STATE_IS_START, false)
                updateUiState { it.copy(isStartButton = false) }
            }

            is HomeContract.UiEvents.StopClicked -> {
                stopLocationServiceUseCase.execute()
                savedStateHandle.set(SAVED_STATE_IS_START, true)
                updateUiState { it.copy(isStartButton = true) }
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
        startLocationServiceUseCase.execute()

        viewModelScope.launch(coExceptionHandler) {
            locationInMemoryCache.cache.collect { latestLocation ->

                latestLocation?.let { location ->
                    hikingPhotoRepository.fetchAndInsertPhoto(location)
                }
            }
        }
    }

    private fun observeHikingPhotos() {
        viewModelScope.launch(coExceptionHandler) {
            hikingPhotoRepository.getHikingPhotosStream().collect { hikingPhotos ->
                updateUiState {
                    it.copy(
                        hikingPhotos = hikingPhotos
                    )
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkStateProvider.observeNetwork().collect { isOnline ->
                Log.d(javaClass.name, "TINTIN is online: $isOnline")
                if (isOnline) {
                    onlineState()
                } else {
                    offlineState()
                }
            }
        }
    }

    private fun onlineState() {
        observeLocationAndFetchPhotos()
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
        )

        const val SAVED_STATE_IS_START = "saved_state_is_start"
    }
}

//Reproduce Error
// GIVEN start tracking > go offline > system process death
// WHEN app opened
// THEN Displays error generic error and start/stop is not disabled

// EXPECTED: displays offline message and start/stop is disabled

// Likely the network observer doesn't act quickly enough
// or because it still has the same state (offline) it doesn't trigger


//TODO:
// - BUG: Fetching two images at a time -> When State is STOP and internet state changes it fetches data
// - BUG: When offline and process death occurs we don't check the network state
// - Think about how we want errors to affect the user experience
//   - If there is an error that benefits from a retry we should retry it
//   - If one network call returns an error we should handle it silently
//   - Not all errors need to be displayed, many can be silent errors that print to the log only
// - Add Error handling, check all error FlickrApi can send and exponential backoff, see android offline documentation
//   - How to handle these states
//      - Offline but user presses start/stop -> offline message should display and network calls should be blocked to prevent Http IO Exception
// - Observe state of notification and location permission
// - Improve error handling infinite loop
// - Check if compose is recomposing a lot, considering using a key with the LazyColumn
// - What errors do we need to handle from Location?
// - Improve notification messaging
// - Display dialog when user clicks reset
// - Display reset button only where there is data to delete
// - All IOException not being caught as one in ExceptionHandlerImpl

//TODO: Error Handling Ideas
// - First retry an error, if it fails after three total attempt
//   display and error for a few seconds and carry on

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

