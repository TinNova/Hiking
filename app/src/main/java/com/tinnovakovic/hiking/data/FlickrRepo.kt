package com.tinnovakovic.hiking.data

import android.location.Location
import javax.inject.Inject

class FlickrRepo @Inject constructor(
    private val flickrApi: FlickrApi
) {

    suspend fun getFlickrPhoto(location: Location): FlickrPhotos {
        return flickrApi.getFlickrPhotosSearch(
            location.latitude.toString(),
            location.longitude.toString()
        )
    }
}
