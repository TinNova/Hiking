package com.tinnovakovic.hiking.data.photo.models

data class FlickrPhotos(
    val photos: Photos
)

data class Photos(
    val photo: List<Photo>,
)

data class Photo(
    val id: String,
    val secret: String,
    val server: String
)
