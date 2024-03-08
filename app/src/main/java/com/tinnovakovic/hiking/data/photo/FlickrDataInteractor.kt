package com.tinnovakovic.hiking.data.photo

import com.tinnovakovic.hiking.data.photo.models.FlickrPhotos
import com.tinnovakovic.hiking.data.photo.models.HikingPhoto
import com.tinnovakovic.hiking.data.photo.models.HikingPhotoEntity
import javax.inject.Inject

class FlickrDataInteractor @Inject constructor() {

    fun mapFlickrPhotoToHikingPhotoEntity(flickrPhoto: FlickrPhotos): List<HikingPhotoEntity> {
        return flickrPhoto.photos.photo.map {
            HikingPhotoEntity(
                photo =
                "$FLICKR_IMAGE_HOST${it.server}/${it.id}_${it.secret}_$FLICKR_IMAGE_SIZE$FLICKR_IMAGE_FORMAT"
            )
        }
    }


}

fun HikingPhotoEntity.asExternalModel() = HikingPhoto(photo = photo)


const val FLICKR_IMAGE_HOST = "https://live.staticflickr.com/"
const val FLICKR_IMAGE_SIZE = "b"
const val FLICKR_IMAGE_FORMAT = ".jpg"
