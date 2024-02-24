package com.tinnovakovic.hiking.shared

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<E: BaseUiEvent, S: BaseUiState>: ViewModel() {

    abstract fun onUiEvent(event: E)

    protected open val _uiState: MutableStateFlow<S>
        get() = throw IllegalArgumentException("UiState Not Initialised")

    open val uiState: StateFlow<S> by lazy { _uiState}

    protected fun updateUiState(updateBlock: (oldState: S) -> S) {
        _uiState.update { updateBlock(it) }
    }
}