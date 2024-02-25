package com.tinnovakovic.hiking.domain

import android.location.Location
import com.tinnovakovic.hiking.data.FlickrRepo
import com.tinnovakovic.hiking.data.Photo
import javax.inject.Inject

class GetPhotoFromLocationUseCase @Inject constructor(
    private val flickrRepo: FlickrRepo,
    private val mapFlickrPhotoToHikingPhotosUseCase: MapFlickrPhotoToHikingPhotosUseCase
) {

    private val photosSet = mutableSetOf<HikingPhoto>()

    suspend fun execute(location: Location): Set<HikingPhoto> {
        val flickrPhoto = flickrRepo.getFlickrPhoto(location)
        val hikingPhotos = mapFlickrPhotoToHikingPhotosUseCase.execute(flickrPhoto)

        addFirstDistinctPhotoToSetOrNone(hikingPhotos)

        return photosSet
    }

    private fun addFirstDistinctPhotoToSetOrNone(hikingPhotos: List<HikingPhoto>) {
        if (hikingPhotos.isNotEmpty()) {

            var wasAdded = false
            hikingPhotos.forEach {
                if (!wasAdded) {
                    wasAdded = photosSet.add(it)
                } else {
                    return
                }
            }
        }
    }
}


