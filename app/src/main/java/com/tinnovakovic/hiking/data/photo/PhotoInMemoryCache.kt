package com.tinnovakovic.hiking.data.photo

import com.tinnovakovic.hiking.domain.photo.HikingPhoto
import com.tinnovakovic.hiking.shared.InMemoryCache
import kotlinx.coroutines.flow.MutableStateFlow
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