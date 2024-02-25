package com.tinnovakovic.hiking.data

import com.tinnovakovic.hiking.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("?method=flickr.photos.search&api_key=${BuildConfig.FLICKR_API_KEY}&radius=0.1&format=json&per_page=1&nojsoncallback=1")
    suspend fun getFlickrPhotosSearch(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): FlickrPhotos?

}

//https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2b093792f19bf70cd4c84ed4ea2fba50&radius=0.1&format=json&per_page=1&lat=47.883437&lon=7.0985995