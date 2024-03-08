package com.tinnovakovic.hiking.data.photo

import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HikingPhotoRepository @Inject constructor(
    private val hikingPhotoDao: HikingPhotoDao,
    private val flickrRepo: FlickrRepo,
    private val flickrDataInteractor: FlickrDataInteractor
) {

    fun getHikingPhotosStream(): Flow<List<HikingPhoto>> {
        return hikingPhotoDao.getAll().map {
            it.map { hikingPhotoEntity ->
                hikingPhotoEntity.asExternalModel()
            }
        }
    }

    suspend fun fetchAndInsertPhoto(location: Location) {
        val flickrPhotos: FlickrPhotos = flickrRepo.fetchFlickrPhoto(location)
        val hikingPhotoEntities: List<HikingPhotoEntity> =
            flickrDataInteractor.mapFlickrPhotoToHikingPhotoEntity(flickrPhotos)

        insertDistinctPhoto(hikingPhotoEntities)
    }

    suspend fun clearDatabase() {
        hikingPhotoDao.deleteAll()
    }

    private suspend fun insertDistinctPhoto(hikingPhotoEntities: List<HikingPhotoEntity>) {
        hikingPhotoEntities.forEach { photo ->
            val result: Array<Long> = hikingPhotoDao.insertHikingPhoto(photo)
            if (result.first() != -1L) return
        }
    }

}
