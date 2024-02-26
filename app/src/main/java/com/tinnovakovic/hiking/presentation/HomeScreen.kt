package com.tinnovakovic.hiking.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.shared.DestroyLifecycleHandler
import com.tinnovakovic.hiking.shared.PauseLifecycleHandler
import com.tinnovakovic.hiking.shared.ResumeLifecycleHandler
import com.tinnovakovic.hiking.shared.spacing


@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        uiAction = viewModel::onUiEvent,
    )

    DestroyLifecycleHandler {
        viewModel.onUiEvent(HomeContract.UiEvents.OnDestroy)
    }

    ResumeLifecycleHandler {
        viewModel.onUiEvent(HomeContract.UiEvents.OnResume)
    }

    PauseLifecycleHandler {
        viewModel.onUiEvent(HomeContract.UiEvents.OnPause)
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeContract.UiState,
    uiAction: (HomeContract.UiEvents) -> Unit,
) {
    val scrollState = rememberLazyListState()

    if (uiState.scrollStateToTop) {
        LaunchedEffect(uiState.hikingPhotos) {
            scrollState.scrollToItem(uiState.hikingPhotos.size + 1)
            uiAction.invoke(HomeContract.UiEvents.PostScrollToTop)
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
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    if (uiState.isStartButton) {
                        uiAction.invoke(HomeContract.UiEvents.StartClicked)
                    } else {
                        uiAction.invoke(HomeContract.UiEvents.StopClicked)
                    }
                }
            ) {
                Text(if (uiState.isStartButton) stringResource(R.string.start) else stringResource(R.string.stop))
            }
        }

        if (uiState.isError) {
            Text(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium),
                text = stringResource(R.string.no_internet_message)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            reverseLayout = true,
            state = scrollState
        ) {
            items(uiState.hikingPhotos.size) { index ->
                val hikingPhoto = uiState.hikingPhotos[index]
                AsyncImage(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                    model = hikingPhoto.photo,
                    contentDescription = ""
                )
            }
        }


    }
}

const val SCROLL_OFFSET = -100
