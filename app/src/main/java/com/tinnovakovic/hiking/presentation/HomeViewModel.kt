package com.tinnovakovic.hiking.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.data.LocationInMemoryCache
import com.tinnovakovic.hiking.domain.GetPhotoFromLocationUseCase
import com.tinnovakovic.hiking.domain.HikingPhoto
import com.tinnovakovic.hiking.domain.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.StopLocationServiceUseCase
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
    private val locationInMemoryCache: LocationInMemoryCache
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("Network Error", "message: ${throwable.message}")
        stopLocationServiceUseCase.execute()
        updateUiState {
            it.copy(isError = true, isStartButton = true)
        }
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
        }
    }

    private fun observeLocationAndFetchPhotos() {
        viewModelScope.launch(exceptionHandler) {
            var latestDistinctPhotos: Set<HikingPhoto> = setOf()
            locationInMemoryCache.cache.collect { latestLocation ->

                latestLocation?.let { location ->
                    latestDistinctPhotos =
                        photoFromLocationUseCase.execute(latestDistinctPhotos, location)
                    updateUiState {
                        it.copy(
                            hikingPhotos = latestDistinctPhotos.toMutableStateList()
                        )
                    }

                    Log.d("TINTIN", "ViewModel LocationInMemoryCache: $location")
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
