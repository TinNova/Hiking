package com.tinnovakovic.hiking.domain

import com.tinnovakovic.hiking.data.photo.FlickrPhotos
import com.tinnovakovic.hiking.data.photo.Photo
import com.tinnovakovic.hiking.data.photo.Photos
import com.tinnovakovic.hiking.domain.photo.HikingPhoto
import com.tinnovakovic.hiking.domain.photo.MapFlickrPhotoToHikingPhotosUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MapFlickrPhotoToHikingPhotosUseCaseTest {

    private val sut: MapFlickrPhotoToHikingPhotosUseCase = MapFlickrPhotoToHikingPhotosUseCase()

    @Test
    fun `GIVEN flickrPhoto, WHEN execute, THEN return HikingPhoto`() {
        //GIVEN
        val flickrPhotos =
            FlickrPhotos(
                Photos(
                    listOf(
                        Photo(
                            id = "id",
                            secret = "secret",
                            server = "server"
                        )
                    )
                )
            )

        val expected = listOf(HikingPhoto("https://live.staticflickr.com/server/id_secret_b.jpg"))

        //WHEN
        val result = sut.execute(flickrPhotos)

        //THEN
        assertEquals(expected[0], result[0])
    }
}