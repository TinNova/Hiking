package com.tinnovakovic.hiking.data.photo

import android.location.Location
import javax.inject.Inject

class FlickrRepo @Inject constructor(
    private val flickrApi: FlickrApi
) {

    suspend fun fetchFlickrPhoto(location: Location): FlickrPhotos? {
        return flickrApi.getFlickrPhotosSearch(
            location.latitude.toString(),
            location.longitude.toString()
        )
    }
}
