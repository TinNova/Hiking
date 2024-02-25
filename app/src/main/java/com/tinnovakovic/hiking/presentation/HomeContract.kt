package com.tinnovakovic.hiking.presentation

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tinnovakovic.hiking.domain.HikingPhoto
import com.tinnovakovic.hiking.shared.BaseUiEvent
import com.tinnovakovic.hiking.shared.BaseUiState
import com.tinnovakovic.hiking.shared.BaseViewModel
import javax.annotation.concurrent.Immutable

interface HomeContract {

    abstract class ViewModel : BaseViewModel<UiEvents, UiState>()

    @Immutable
    data class UiState(
        val isStartButton: Boolean,
        val hikingPhotos: SnapshotStateList<HikingPhoto>,
        val scrollStateToTop: Boolean,
        val isError: Boolean
    ) : BaseUiState {}

    sealed class UiEvents : BaseUiEvent {
        data object StartClicked : UiEvents()
        data object StopClicked : UiEvents()
        data object OnDestroy : UiEvents()
        data object OnResume : UiEvents()
    }
}