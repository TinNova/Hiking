package com.tinnovakovic.hiking.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.HikingPhotoRepository
import com.tinnovakovic.hiking.domain.location.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.location.StopLocationServiceUseCase
import com.tinnovakovic.hiking.shared.ContextProvider
import com.tinnovakovic.hiking.shared.network.NetworkStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startLocationServiceUseCase: StartLocationServiceUseCase,
    private val stopLocationServiceUseCase: StopLocationServiceUseCase,
    private val locationInMemoryCache: LocationInMemoryCache,
    private val hikingPhotoRepository: HikingPhotoRepository,
    private val networkStateProvider: NetworkStateProvider,
    private val contextProvider: ContextProvider,
    private val savedStateHandle: SavedStateHandle
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())

    private var initializeCalled = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is IOException -> contextProvider.getContext().getString(R.string.generic_error_message)
            else -> contextProvider.getContext().getString(R.string.generic_error_message)
        }
        updateUiState {
            it.copy(errorMessage = errorMessage)
        }
        // keep trying until user stops it or app terminates.
        // try with exponential backoff
        observeLocationAndFetchPhotos()
        observeHikingPhotos()
    }

    @MainThread
    private fun initialise() {
        if (initializeCalled) return
        initializeCalled = true

        observeNetwork()
        restoreSavedState()
    }

    private fun restoreSavedState() {
        viewModelScope.launch {
            savedStateHandle.get<Boolean>(SAVED_STATE_IS_START)?.let { isStart ->
                if (isStart) {
                    updateUiState { it.copy(isStartButton = true) }
                    observeHikingPhotos()
                } else {
                    updateUiState { it.copy(isStartButton = false) }
                    observeLocationAndFetchPhotos()
                    observeHikingPhotos()
                }
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

        viewModelScope.launch(exceptionHandler) {
            locationInMemoryCache.cache.collect { latestLocation ->

                latestLocation?.let { location ->
                    hikingPhotoRepository.fetchAndInsertPhoto(location)
                }
            }
        }
    }

    private fun observeHikingPhotos() {
        viewModelScope.launch {
            hikingPhotoRepository.getHikingPhotosStream().collect { hikingPhotos ->
                updateUiState {
                    it.copy(
                        hikingPhotos = hikingPhotos,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkStateProvider.observeNetwork().collect { isOnline ->
                updateUiState {
                    if (isOnline) {
                        observeLocationAndFetchPhotos()
                        it.copy(errorMessage = null)
                    } else {
                        stopLocationServiceUseCase.execute()
                        it.copy(errorMessage = contextProvider.getContext().getString(R.string.offline_message))
                    }
                }
            }
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true,
            hikingPhotos = listOf(),
            scrollStateToTop = false,
            errorMessage = null
        )

        const val SAVED_STATE_IS_START = "saved_state_is_start"
    }
}

//TODO:
//   - How to handle these states
//      - Offline but user presses start/stop -> offline message should display and network calls should be blocked to prevent Http IO Exception
// - Check if compose is recomposing a lot, considering using a key with the LazyColumn
// - Add button to reset photos/hike
// - Add Error handling, check all error FlickrApi can send and exponential backoff, see android offline documentation
// - Observe state of notification and location permission
// - Improve error handling infinite loop
// - Improve notification messaging

//DONE:
// - Save photos in Room
// - Have a savedStateHandle save a boolean
//   -- After process death recovery it should be true, then display the images and start location tracking again
// - Should database return a list of HikingPhotoEntity or HikingPhoto? Check offline app documentation
// - Observe internet state on initialise
