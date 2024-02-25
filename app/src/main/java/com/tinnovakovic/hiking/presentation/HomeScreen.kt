package com.tinnovakovic.hiking.presentation


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tinnovakovic.hiking.shared.DestroyLifecycleHandler
import com.tinnovakovic.hiking.shared.ResumeLifecycleHandler

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
}

@Composable
fun HomeScreenContent(
    uiState: HomeContract.UiState,
    uiAction: (HomeContract.UiEvents) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
            Text(if (uiState.isStartButton) "Start" else "Stop")
        }

        if (uiState.isError) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "No Internet, please check your internet connection and press start button to try again."
            )
        }
        val scrollState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            reverseLayout = true,
            state = scrollState
        ) {
            items(uiState.photos.size) { index ->
                val photo = uiState.photos[index]
                AsyncImage(
                    modifier = Modifier.padding(vertical = 8.dp),
                    model = "$FLICKR_IMAGE_HOST${photo.server}/${photo.id}_${photo.secret}_b.jpg",
                    contentDescription = ""
                )
            }
        }

        if (uiState.scrollStateToTop) {
            LaunchedEffect(uiState.photos) {
                scrollState.animateScrollToItem(uiState.photos.size + 1, scrollOffset = SCROLL_OFFSET)
            }
        }

    }
}

const val FLICKR_IMAGE_HOST = "https://live.staticflickr.com/"
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
// -- To ensure we're updating the list, download more photos and check that they don't exist in the list already, right now we download one, but if it's not distinct then we have missed displaying one.
// -- Handle no internet case and Exceptions -> High Priority
// -- There is still a crash when null is returned somehow. Try navigating in unpopulated area -> High Priority

// -- LOW PRIORITY ITEMS
// -- Check if the images are recomposing? If they are give each item a key? -> Low Priority
// -- Filter API for only outdoor and nature photos? To Avoid photos of food of example -> Not specified in requirements

// -- DONE ITEMS
// -- scroll to latest photo on onResume -> High Priority - DONE
// -- Ensure we don't display the same image twice - DONE
// -- Ask user for notification permissions - DONE

// The Crash:
//java.lang.NullPointerException: Parameter specified as non-null is null: method kotlin.collections.CollectionsKt___CollectionsKt.firstOrNull, parameter <this>
//at kotlin.collections.CollectionsKt___CollectionsKt.firstOrNull(Unknown Source:2)
//at com.tinnovakovic.hiking.domain.GetPhotoFromLocationUseCase.execute(getPhotoFromLocationUseCase.kt:14)