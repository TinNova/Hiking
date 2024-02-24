package com.tinnovakovic.hiking.presentation

import androidx.lifecycle.viewModelScope
import com.tinnovakovic.hiking.domain.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.StopLocationServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startLocationServiceUseCase: StartLocationServiceUseCase,
    private val stopLocationServiceUseCase: StopLocationServiceUseCase
) : HomeContract.ViewModel() {

    override val _uiState: MutableStateFlow<HomeContract.UiState> =
        MutableStateFlow(initialUiState())
    override fun onUiEvent(event: HomeContract.UiEvents) {
        when (event) {
            is HomeContract.UiEvents.StartClicked -> {
                // 1st ensure the geofence and broadcastReceiver works with CleanArchitecture
                // 2nd get users location and start the first geofence with it
                viewModelScope.launch {
                    startLocationServiceUseCase.execute()
                }
            }
            is HomeContract.UiEvents.StopClicked -> {}
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            string = ""
        )
    }

}

