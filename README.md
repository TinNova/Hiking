# Hiking - Android App
This is a portfolio project that demonstrates the use of Compose, Coroutines including Flows, Hilt, Jetpack Navigation, ViewModel, MVI and Clean Architecture.

### App Functionality
Tracks your location and uses it to fetch and display a photo from Flickr every 100 metres.

Open the app, press start and put your phone in your pocket, now go on a hike in nature or the city while the app fetches photos using your location, at the end of your hike you will have a visual representation of your walk in the form of a list of photos of the places you've been.

## Screen Shots

| Initial State | Tracking State | Foreground Notification |
|     :---:     |     :---:     |     :---:     |
|<img src="https://i.imgur.com/rVAUvYe.png" width="225px" height="60%" align="centre">|<img src="https://i.imgur.com/UJHK4V6.png" width="225px" height="60%" align="centre">|<img src="https://i.imgur.com/8mwUiTO.png" width="225px" height="60%" align="centre">|

| Not Tracking State | Offline State | Error State |
|     :---:     |     :---:     |     :---:     |
 |<img src="https://i.imgur.com/WJAStK9.png" width="225px" height="60%" align="centre">|<img src="https://i.imgur.com/BICgjD1.png" width="225px" height="60%" align="centre">|<img src="https://i.imgur.com/Q6R2sXg.png" width="225px" height="60%" align="centre">|
 
## Technical Challenges
- Track the users location using a Foreground service.
- Observe location emissions with Coroutine Flow.
- Observe the devices network connection and automatically restart fetching images when a connection is re-established.
- Handle system process death, Room is used to persist the images fetched ensuring the photos are available in case of a system process death.
- Automatically recover from errors, the device will be in the users pocket therefore we need to fail and recover to continue to track their location and fetch photos.

## Architecture
### Presentation Layer
The app is built in MVI, where each UI 'screen' has its own ViewModel, which exposes a single StateFlow containing the entire view state. Each ViewModel is responsible for subscribing to any data streams and objects required for the view, as well as exposing functions which allow the UI to send events.

Using the HomeScreen as an example within the <code>[com.tinnovakovic.hiking.presentation](https://github.com/TinNova/Hiking/tree/master/app/src/main/java/com/tinnovakovic/hiking/presentation)</code> package:

- The ViewModel is implemented as <code>[HomeViewModel](https://github.com/TinNova/Hiking/blob/master/app/src/main/java/com/tinnovakovic/hiking/presentation/HomeViewModel.kt)</code>, which exposes a MutableStateFlow<HomeContract> for the UI to observe.
- <code>[HomeContract](https://github.com/TinNova/Hiking/blob/master/app/src/main/java/com/tinnovakovic/hiking/presentation/HomeContract.kt)</code> contains the complete view state for the home screen as an @Immutable data class ```UiState()```. It also exposes the functions which enable the UI to send events to the ViewModel in the form of a sealed class ```sealed class UiEvents : BaseUiEvent```.
- The Compose <code>[HomeScreen](https://github.com/TinNova/Hiking/blob/master/app/src/main/java/com/tinnovakovic/hiking/presentation/HomeScreen.kt)</code> uses HomeViewModel, and observes it's UiState as Compose State, using ```collectAsStateWithLifecycle()```:
```
val viewModel = hiltViewModel<HomeViewModel>()
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```
This MVI pattern is made scalable by inheriting the base classes in <code>[com.tinnovakovic.hiking.shared.mvi](https://github.com/TinNova/Hiking/tree/master/app/src/main/java/com/tinnovakovic/hiking/shared/mvi)</code>, ```BaseUiEvent```, ```BaseUiState``` and ```BaseViewModel```

### App Architecture
The app uses Clean Architecture. 
- The Data layer integrates Networking sources from Retrofit and Persistent sources from Room using the Repository pattern, furthermore it exposes UI models mapped from the Network and Persistent models which are incapsulated within the Data layer.
- The Domain layer manages business logic using the UseCase pattern.
- The Presentation layer uses the MVI pattern as described above.

## Tech-Stack

* Kotlin
* Dagger Hilt
* Coroutines
* Compose
* Architecture
  * Clean Architecture
  * MVI
* Jetpack Navigation 
* Testing
  * JUnit5
  * Mockk

##  🚧 Work In Progress  🚧
The app isn't completed (is any app "completed"?), here are the additional features and technical debt that is planned:
#### Features
- Add a Scroll To Top button when a new photo is added.
- Take user to top of the list when opening the app.
- Format horizontal photos to stretch to the width of the screen.

Speculative Features:
- Add a PreviousHikesScreen to display a users previous hikes.

#### Technical Debt
- Unit Testing the HomeViewModel, (it's frustrating having to manually test the app everytime I edit it to ensure regression hasn't occured).
- Retry a failed photo fetch with exponential backoff.
- Investigate error handling and how suspend errors marry up with flow errors, (error handling works as expected, but it's worth researching it to see if it can be improved).

## To Run The App
You will need a flickr api key and insert it into local.properties: <br/>
```
FLICKR_API_KEY = {api_key}
```

