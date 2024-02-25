package com.tinnovakovic.hiking.presentation


import androidx.compose.foundation.layout.Column
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
import com.tinnovakovic.hiking.spacing

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
    Column(
        Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
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

        if (uiState.isError) {
            Text(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium),
                text = stringResource(R.string.no_internet_message)
            )
        }
        val scrollState = rememberLazyListState()

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

        if (uiState.scrollStateToTop) {
            LaunchedEffect(uiState.hikingPhotos) {
                scrollState.animateScrollToItem(uiState.hikingPhotos.size + 1, scrollOffset = SCROLL_OFFSET)
            }
        }

    }
}

const val SCROLL_OFFSET = -100


//TODO: We continue tracking even after app terminates...


// Why To Avoid Background Location
// --It feels like spying and Google wants to avoid this, instead use foreground service which will only work when activity is visible, or when not visible a notification is displayed whilst using their location
// --This way it is transparent and obvious to the user that we are using their location.

//TODO: Using ForeGround Services, in order to pass GooglePlay Approval you must adhere to these practises:
// --The use of foreground service must be initiated as a continuation of an in-app, user-initiated action.
// --The use of foreground service must be terminated immediately after the application completes the intended use case of the user-initiated action.
// --Link: https://support.google.com/googleplay/android-developer/answer/9799150#Accessing%20location%20in%20the%20foreground

//TODO:


// -- LOW PRIORITY ITEMS
// -- Check if the images are recomposing? If they are give each item a key? -> Low Priority
// -- Filter API for only outdoor and nature photos? To Avoid photos of food of example -> Not specified in requirements

// -- DONE ITEMS
// -- GetPhotoFromLocationUseCase Can't be tested because of global var
// -- Improve the scroll to top logic when onResume is called
// -- Create an extension function to create the Coil image URL - DONE
// -- Move hardcoded strings and padding values to String Res file - DONE
// -- Handle no internet case - DONE
// -- To ensure we're updating the list, download more photos and check that they don't exist in the list already, right now we download one, but if it's not distinct then we have missed displaying one. - DONE
// -- scroll to latest photo on onResume -> High Priority - DONE
// -- Ensure we don't display the same image twice - DONE
// -- Ask user for notification permissions - DONE

// The Crash:
//java.lang.NullPointerException: Parameter specified as non-null is null: method kotlin.collections.CollectionsKt___CollectionsKt.firstOrNull, parameter <this>
//at kotlin.collections.CollectionsKt___CollectionsKt.firstOrNull(Unknown Source:2)
//at com.tinnovakovic.hiking.domain.GetPhotoFromLocationUseCase.execute(GetPhotoFromLocationUseCase.kt:14)

// -> Can't reproduce the error, I tried navigating down a country road where no images exist.