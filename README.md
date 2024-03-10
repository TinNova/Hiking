# Hiking - Android App

### Summary
This is a portfolio project that demonstrates the use of Compose, Coroutines including Flows, Hilt, Jetpack Navigation, ViewModel, MVI and Clean Architecture.

### App Functionality
Tracks your location and uses it to fetch and display a photo from Flickr every 100 metres.

Use case: Open the app, press start and put the phone in your pocket, then go on a walk in nature or the city while the app fetches photos using your location, at the end of your walk you will have a visual representation of your walk.

### Technical Challenges
- Track the users location using a Foreground service
- Observe location emissions with Coroutine Flow
- Observe the devices network connection and automatically restart fetching images when a connection is reestablished
- Handle system process death, Room is used to persist the images fetched ensuring the photos are available in case of a system process death
- Automatically recover from errors, the device will be in the users pocket therefore we need to silently fail and continue to track the location and fetch photos

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

## Screen Shots

| Initial State | Tracking State | Foreground Notification |
|     :---:     |     :---:     |     :---:     |
|<img src="https://i.imgur.com/rVAUvYe.png" width="60%" height="60%" align="centre">|<img src="https://i.imgur.com/UJHK4V6.png" width="60%" height="60%" align="centre">|<img src="https://i.imgur.com/8mwUiTO.png" width="60%" height="60%" align="centre">|

| Not Tracking State | Offline State | Error State |
|     :---:     |     :---:     |     :---:     |
 |<img src="https://i.imgur.com/WJAStK9.png" width="60%" height="60%" align="centre">|<img src="https://i.imgur.com/BICgjD1.png" width="60%" height="60%" align="centre">|<img src="https://i.imgur.com/Q6R2sXg.png" width="60%" height="60%" align="centre">|

## To Run The App
You will need a flickr api key and insert it into local.properties: <br/>
```
FLICKR_API_KEY = {api_key}
```

