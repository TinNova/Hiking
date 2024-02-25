package com.tinnovakovic.hiking.domain

import android.location.Location
import com.tinnovakovic.hiking.data.FlickrRepo
import com.tinnovakovic.hiking.data.Photo
import javax.inject.Inject

class GetPhotoFromLocationUseCase @Inject constructor(
    private val flickrRepo: FlickrRepo
) {

    private val photos = mutableSetOf<Photo>()

    suspend fun execute(location: Location): Set<Photo> {
        //TODO How to handle errors and exceptions?
        val latestPhoto = flickrRepo
            .getFlickrPhoto(location)
            .photos.photo.firstOrNull()

        if (latestPhoto != null) {
            photos.add(latestPhoto)
        }

        return photos
    }
}
