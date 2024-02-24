package com.tinnovakovic.hiking.presentation


import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tinnovakovic.hiking.shared.DestroyLifecycleHandler

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
}

@Composable
fun HomeScreenContent(
    uiState: HomeContract.UiState,
    uiAction: (HomeContract.UiEvents) -> Unit,
) {
    OutlinedButton(onClick = {
        uiAction.invoke(HomeContract.UiEvents.StartClicked)
    }) {
        Text(text = "Start")
    }
}

//TODO: We continue tracking even after app terminates...


const val TIME_BETWEEN_UPDATES = 1L // one seconds

// Why To Avoid Background Location
// --It feels like spying and Google wants to avoid this, instead use foreground service which will only work when activity is visible, or when not visible a notification is displayed whilst using their location
// --This way it is transparent and obvious to the user that we are using their location.

//TODO: Using ForeGround Services, in order to pass GooglePlay Approval you must adhere to these practises:
// --The use of foreground service must be initiated as a continuation of an in-app, user-initiated action.
// --The use of foreground service must be terminated immediately after the application completes the intended use case of the user-initiated action.
// --Link: https://support.google.com/googleplay/android-developer/answer/9799150#Accessing%20location%20in%20the%20foreground

//TODO:
// -- Ask user for notification permissions - DONE