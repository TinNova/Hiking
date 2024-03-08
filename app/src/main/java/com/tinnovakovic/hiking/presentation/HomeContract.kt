package com.tinnovakovic.hiking.presentation

import com.tinnovakovic.hiking.data.photo.HikingPhoto
import com.tinnovakovic.hiking.shared.mvi.BaseUiEvent
import com.tinnovakovic.hiking.shared.mvi.BaseUiState
import com.tinnovakovic.hiking.shared.mvi.BaseViewModel
import javax.annotation.concurrent.Immutable

interface HomeContract {

    abstract class ViewModel : BaseViewModel<UiEvents, UiState>()

    @Immutable
    data class UiState(
        val isStartButton: Boolean,
        val hikingPhotos: List<HikingPhoto>,
        val scrollStateToTop: Boolean,
        val errorMessage: String?,
    ) : BaseUiState {}

    sealed class UiEvents : BaseUiEvent {
        data object Initialise : UiEvents()
        data object StartClicked : UiEvents()
        data object StopClicked : UiEvents()
        data object ResetClicked : UiEvents()
        data object OnDestroy : UiEvents()
        data object OnResume : UiEvents()
        data object OnPause : UiEvents()
        data object PostScrollToTop : UiEvents()
    }
}