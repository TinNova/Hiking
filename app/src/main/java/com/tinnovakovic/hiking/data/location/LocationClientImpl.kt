package com.tinnovakovic.hiking.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.tinnovakovic.hiking.shared.hasLocationPermission
import com.tinnovakovic.hiking.shared.ContextProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationClientImpl @Inject constructor(
    contextProvider: ContextProvider,
    private val client: FusedLocationProviderClient
) : LocationClient {

    private val context = contextProvider.getContext()
    private var savedLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                TimeUnit.SECONDS.toMillis(SECONDS_BETWEEN_UPDATES)
            ).build()


            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)


                    result.locations.lastOrNull()?.let { location ->
                        val currentSavedLocation: Location? = savedLocation
                        if (currentSavedLocation == null || currentSavedLocation.distanceTo(location) >= 100f) {
                            savedLocation = location
                            launch { send(location) }
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            // called when coroutine is cancelled
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }

    }

    companion object {
        const val SECONDS_BETWEEN_UPDATES = 1L // one seconds
    }
}