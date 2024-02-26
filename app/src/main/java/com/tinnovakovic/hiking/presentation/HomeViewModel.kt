package com.tinnovakovic.hiking.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.PhotoInMemoryCache
import com.tinnovakovic.hiking.domain.photo.GetPhotoFromLocationUseCase
import com.tinnovakovic.hiking.domain.photo.HikingPhoto
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
    private val photoFromLocationUseCase: GetPhotoFromLocationUseCase,
    private val locationInMemoryCache: LocationInMemoryCache,
    private val photoInMemoryCache: PhotoInMemoryCache
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        updateUiState {
            it.copy(isError = true)
        }
        // keep trying until user stops it or app terminates.
        observeLocationAndFetchPhotos()
    }

    override fun onUiEvent(event: HomeContract.UiEvents) {
        when (event) {
            is HomeContract.UiEvents.StartClicked -> {
                startLocationServiceUseCase.execute()

                updateUiState {
                    it.copy(isStartButton = false, isError = false)
                }

                observeLocationAndFetchPhotos()
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
                    val latestDistinctPhotos: Set<HikingPhoto> = photoInMemoryCache.cache.value
                    val photos = photoFromLocationUseCase.execute(latestDistinctPhotos, location)
                    photoInMemoryCache.updateCache(photos)

                    updateUiState {
                        it.copy(
                            hikingPhotos = photos.toMutableStateList(),
                            isError = false
                        )
                    }
                }
            }
        }
    }


    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true,
            hikingPhotos = mutableStateListOf(),
            scrollStateToTop = false,
            isError = false
        )
    }
}
