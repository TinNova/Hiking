package com.tinnovakovic.hiking.presentation

import com.tinnovakovic.hiking.shared.BaseUiEvent
import com.tinnovakovic.hiking.shared.BaseUiState
import com.tinnovakovic.hiking.shared.BaseViewModel
import javax.annotation.concurrent.Immutable

interface HomeContract {

    abstract class ViewModel : BaseViewModel<UiEvents, UiState>()

    @Immutable
    data class UiState(
        val string: String
//        val subjects: List<Subject>,
//        val movies: List<Movie>
    ) : BaseUiState {}

    sealed class UiEvents : BaseUiEvent {
        data object StartClicked : UiEvents()
        data object StopClicked : UiEvents()
        data object OnDestroy : UiEvents()
    }
}