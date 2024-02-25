package com.tinnovakovic.hiking.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.data.LocationInMemoryCache
import com.tinnovakovic.hiking.data.Photo
import com.tinnovakovic.hiking.domain.GetPhotoFromLocationUseCase
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
        Log.d("Network Error", "${throwable.message}")

        //TODO: If it's an internet connection, ask user to try again.
        // -- If it's another error then tell user there is another error, try again.
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
                    it.copy(isStartButton = false, scrollStateToTop = false, isError = false)
                }

                viewModelScope.launch(exceptionHandler) {
                    locationInMemoryCache.cache.collect { latestLocation ->

                        latestLocation?.let { location ->
                            val latestDistinctPhotos: Set<Photo> =
                                photoFromLocationUseCase.execute(location)
                            updateUiState {
                                it.copy(
                                    photos = latestDistinctPhotos.toMutableStateList(),
                                    scrollStateToTop = false
                                )
                            }

                            Log.d("TINTIN", "ViewModel LocationInMemoryCache: $location")
                        }

                    }
                }
            }

            is HomeContract.UiEvents.StopClicked -> {
                stopLocationServiceUseCase.execute()
                updateUiState {
                    it.copy(isStartButton = true, scrollStateToTop = false)
                }
            }

            is HomeContract.UiEvents.OnDestroy -> stopLocationServiceUseCase.execute()
            is HomeContract.UiEvents.OnResume -> {
                updateUiState { it.copy(scrollStateToTop = true) }
            }
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true,
            photos = mutableStateListOf(),
            scrollStateToTop = false,
            isError = false
        )
    }
}
