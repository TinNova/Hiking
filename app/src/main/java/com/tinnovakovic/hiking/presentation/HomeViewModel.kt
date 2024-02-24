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
                startLocationServiceUseCase.execute()
                updateUiState {
                    it.copy(isStartButton = false)
                }
            }
            is HomeContract.UiEvents.StopClicked -> {
                stopLocationServiceUseCase.execute()
                updateUiState {
                    it.copy(isStartButton = true)
                }
            }
            is HomeContract.UiEvents.OnDestroy -> stopLocationServiceUseCase.execute()
        }
    }

    private companion object {
        fun initialUiState() = HomeContract.UiState(
            isStartButton = true
        )
    }

}

