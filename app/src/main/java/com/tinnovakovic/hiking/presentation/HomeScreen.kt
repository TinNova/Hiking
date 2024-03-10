package com.tinnovakovic.hiking.presentation


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.presentation.HomeContract.UiEvents
import com.tinnovakovic.hiking.shared.DestroyLifecycleHandler
import com.tinnovakovic.hiking.shared.ErrorToUser
import com.tinnovakovic.hiking.shared.PauseLifecycleHandler
import com.tinnovakovic.hiking.shared.ResumeLifecycleHandler
import com.tinnovakovic.hiking.shared.spacing
import kotlinx.coroutines.delay


@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        uiAction = viewModel::onUiEvent,
    )

    DestroyLifecycleHandler {
        viewModel.onUiEvent(UiEvents.OnDestroy)
    }

    ResumeLifecycleHandler {
        viewModel.onUiEvent(UiEvents.OnResume)
    }

    PauseLifecycleHandler {
        viewModel.onUiEvent(UiEvents.OnPause)
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeContract.UiState,
    uiAction: (UiEvents) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(true) {
        // This instead on using init{} in viewModel to prevent race condition
        uiAction(UiEvents.Initialise)
    }

    if (uiState.scrollStateToTop) {
        LaunchedEffect(uiState.hikingPhotos) {
            scrollState.scrollToItem(uiState.hikingPhotos.size + 1)
            uiAction.invoke(UiEvents.PostScrollToTop)
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    uiAction.invoke(UiEvents.ResetClicked)
                }) {
                Text(text = stringResource(R.string.reset))
            }
            OutlinedButton(
                enabled = uiState.isStartStopButtonEnabled,
                onClick = {
                    if (uiState.isStartButton) {
                        uiAction.invoke(UiEvents.StartClicked)
                    } else {
                        uiAction.invoke(UiEvents.StopClicked)
                    }
                }
            ) {
                Text(if (uiState.isStartButton) stringResource(R.string.start) else stringResource(R.string.stop))
            }
        }

        if (uiState.errorMessage != null) {
            Log.d("TINTIN", "TINTIN errormessage: ")
            when (uiState.errorMessage) {
                is ErrorToUser.LocationError -> {
                    Log.d("TINTIN", "TINTIN errormessage location: ${uiState.errorMessage}")
                    ErrorText(uiState.errorMessage.message)
                }

                is ErrorToUser.GenericError -> {
                    Log.d("TINTIN", "TINTIN errormessage generic: ${uiState.errorMessage}")
                    var show by remember { mutableStateOf(true) }
                    LaunchedEffect(key1 = Unit) {
                        delay(ERROR_MESSAGE_TIMEOUT)
                        show = false
                    }
                    if (show) {
                        ErrorText(uiState.errorMessage.message)
                    }
                }
            }
        }

        LazyColumn(
            reverseLayout = true,
            state = scrollState
        ) {
            items(
                items = uiState.hikingPhotos,
                key = { it.photo }
            ) { hikingPhoto ->

                AsyncImage(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                    model = hikingPhoto.photo,
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
fun ErrorText(errorMessage: String) {
    Text(
        modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium),
        text = errorMessage
    )
}

private const val ERROR_MESSAGE_TIMEOUT = 2500L
