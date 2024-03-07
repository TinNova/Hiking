package com.tinnovakovic.hiking.presentation

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.HikingPhotoRepository
import com.tinnovakovic.hiking.domain.location.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.location.StopLocationServiceUseCase
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
    private val savedStateHandle: SavedStateHandle
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())

    private var initializeCalled = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        updateUiState {
            it.copy(isError = true)
        }
        // keep trying until user stops it or app terminates.
        observeLocationAndFetchPhotos()
        observeHikingPhotos()
    }

    @MainThread
    private fun initialise() {
        if (initializeCalled) return
        initializeCalled = true

//        restoreSavedState()
//        Log.d("TINTINTEST", "initialise")

    }

//    private fun restoreSavedState() {
//        viewModelScope.launch {
////            savedStateHandle.get<Set<HikingPhoto>>(SAVED_STATE_IMAGES)?.let {
////                Log.d("TINTINTEST", "restored Images, first image: ${it.first()}")
////                displayPhotos(it)
////            }
//        }
//    }

    override fun onUiEvent(event: HomeContract.UiEvents) {
        when (event) {
            is HomeContract.UiEvents.Initialise -> initialise()

            is HomeContract.UiEvents.StartClicked -> {
                startLocationServiceUseCase.execute()

                updateUiState {
                    it.copy(isStartButton = false, isError = false)
                }

                observeLocationAndFetchPhotos()
                observeHikingPhotos()
            }

            is HomeContract.UiEvents.StopClicked -> {
                stopLocationServiceUseCase.execute()
                updateUiState {
                    it.copy(isStartButton = true)
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
        viewModelScope.launch(exceptionHandler) {
            locationInMemoryCache.cache.collect { latestLocation ->

                latestLocation?.let { location ->
                    // fetch and add photo to database
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
                        isError = false
                    )
                }
            }
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true,
            hikingPhotos = listOf(),
            scrollStateToTop = false,
            isError = false
        )

        const val SAVED_STATE_IMAGES = "saved_state_image"
    }
}

//TODO:
// - Observe internet state and check it inside observeLocationAndFetchPhotos()
// - Save photos in Room
// - Have a savedStateHandle save a boolean
//   -- After process death recovery it should be true, then display the images and start location tracking again
// - Should database return a list of HikingPhotoEntity or HikingPhoto? Check offline app documentation
// - Check if compose is recomposing a lot, considering using a key with the LazyColumn
// - Add button to reset photos/hike
// - Add Error handling, check all error FlickrApi can send