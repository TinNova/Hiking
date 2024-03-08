package com.tinnovakovic.hiking.data.photo

import com.tinnovakovic.hiking.BuildConfig
import com.tinnovakovic.hiking.data.photo.models.FlickrPhotos
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("?method=flickr.photos.search&api_key=${BuildConfig.FLICKR_API_KEY}&radius=0.1&format=json&per_page=10&nojsoncallback=1")
    suspend fun getFlickrPhotosSearch(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Result<FlickrPhotos>

}

//https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key={api_key}&radius=0.1&format=json&per_page=1&lat=47.883437&lon=7.0985995