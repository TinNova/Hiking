package com.tinnovakovic.hiking.data

import android.location.Location
import com.tinnovakovic.hiking.domain.HikingPhoto
import com.tinnovakovic.hiking.shared.InMemoryCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoInMemoryCache @Inject constructor() : InMemoryCache<Set<HikingPhoto>> {

    private val _cache = MutableStateFlow<Set<HikingPhoto>>(setOf())
    override val cache = _cache.asStateFlow()

    override suspend fun updateCache(newData: Set<HikingPhoto>) {
        _cache.emit(newData)
    }
}