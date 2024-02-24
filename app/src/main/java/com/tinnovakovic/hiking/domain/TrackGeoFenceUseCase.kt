package com.tinnovakovic.hiking.domain

import android.location.Location
import com.tinnovakovic.hiking.shared.ContextProvider
import javax.inject.Inject

class TrackGeoFenceUseCase @Inject constructor(
    private val contextProvider: ContextProvider
) {

    // this needs to return a flow of GeoFenceObjects
    suspend fun execute() {

//        geofenceBroadcastReceiverRepo.registerBroadcastReceiver() { event ->
//            Log.e("TINTINTEST", "Event: $event")
//        }

//        geofenceManager.addGeofence(
//            "soultz",
//            location = Location("").apply {
//                latitude = 47.8844
//                longitude = 7.2285
//            },
//        )
    }
}
