package com.tinnovakovic.hiking.data

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationInMemoryCache @Inject constructor() : InMemoryCache<Location> {

    private val _cache = MutableStateFlow<Location?>(null)
    override val cache = _cache.asStateFlow()

    override suspend fun updateCache(newData: Location) {
        _cache.emit(newData)
    }
}