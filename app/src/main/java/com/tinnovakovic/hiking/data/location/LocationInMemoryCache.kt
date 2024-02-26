package com.tinnovakovic.hiking.data.location

import android.location.Location
import com.tinnovakovic.hiking.shared.InMemoryCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationInMemoryCache @Inject constructor() : InMemoryCache<Location> {

    private val _cache = MutableStateFlow<Location?>(null)
    override val cache: StateFlow<Location?> = _cache.asStateFlow()

    override suspend fun updateCache(newData: Location) {
        _cache.emit(newData)
    }
}