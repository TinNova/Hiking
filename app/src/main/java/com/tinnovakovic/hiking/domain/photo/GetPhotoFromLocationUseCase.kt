package com.tinnovakovic.hiking.domain.photo

import android.location.Location
import com.tinnovakovic.hiking.data.photo.FlickrRepo
import javax.inject.Inject

class GetPhotoFromLocationUseCase @Inject constructor(
    private val flickrRepo: FlickrRepo,
    private val mapFlickrPhotoToHikingPhotosUseCase: MapFlickrPhotoToHikingPhotosUseCase
) {

    suspend fun execute(
        existingHikingPhotos: Set<HikingPhoto>,
        location: Location
    ): Set<HikingPhoto> {
        val flickrPhoto = flickrRepo.fetchFlickrPhoto(location) ?: return existingHikingPhotos
        val hikingPhotos = mapFlickrPhotoToHikingPhotosUseCase.execute(flickrPhoto)

        return addFirstDistinctPhotoToSetOrNone(existingHikingPhotos.toMutableSet(), hikingPhotos)
    }

    private fun addFirstDistinctPhotoToSetOrNone(
        existingHikingPhotos: MutableSet<HikingPhoto>,
        latestHikingPhotos: List<HikingPhoto>
    ): MutableSet<HikingPhoto> {
        return if (latestHikingPhotos.isNotEmpty()) {

            var wasAdded = false
            latestHikingPhotos.forEach {
                if (!wasAdded) {
                    wasAdded = existingHikingPhotos.add(it)
                } else {
                    return@forEach
                }
            }
            existingHikingPhotos
        } else {
            existingHikingPhotos
        }
    }
}
