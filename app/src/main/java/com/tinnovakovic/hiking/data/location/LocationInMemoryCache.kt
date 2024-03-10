package com.tinnovakovic.hiking.data.location

import android.location.Location
import com.tinnovakovic.hiking.shared.InMemoryCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationInMemoryCache @Inject constructor() : InMemoryCache<LocationEmission> {

    private val _cache = MutableStateFlow<LocationEmission?>(null)
    override val cache: StateFlow<LocationEmission?> = _cache.asStateFlow()

    override suspend fun updateCache(newData: LocationEmission) {
        _cache.update { newData }
    }
}

sealed class LocationEmission {
    data class LocationValue(
        val location: Location
    ): LocationEmission()

    data class LocationException(
        val throwable: Throwable
    ): LocationEmission()
}