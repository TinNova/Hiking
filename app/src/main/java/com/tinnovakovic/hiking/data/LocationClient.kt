package com.tinnovakovic.hiking.data

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(): Flow<Location>

    class LocationException(message: String): Exception()
}