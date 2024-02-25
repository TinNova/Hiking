package com.tinnovakovic.hiking.domain

import com.tinnovakovic.hiking.data.FlickrPhotos
import javax.inject.Inject

class MapFlickrPhotoToHikingPhotosUseCase @Inject constructor() {

    fun execute(flickrPhoto: FlickrPhotos): List<HikingPhoto> {
        return flickrPhoto.photos.photo.map {
            HikingPhoto(
                photo =
                "$FLICKR_IMAGE_HOST${it.server}/${it.id}_${it.secret}_$FLICKR_IMAGE_SIZE$FLICKR_IMAGE_FORMAT"
            )
        }
    }
}

const val FLICKR_IMAGE_HOST = "https://live.staticflickr.com/"
const val FLICKR_IMAGE_SIZE = "b"
const val FLICKR_IMAGE_FORMAT = ".jpg"
