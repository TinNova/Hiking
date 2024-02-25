package com.tinnovakovic.hiking.domain

import com.tinnovakovic.hiking.data.FlickrPhotos
import com.tinnovakovic.hiking.data.FlickrRepo
import com.tinnovakovic.hiking.data.Photo
import com.tinnovakovic.hiking.data.Photos
import io.mockk.mockk
import org.junit.Test
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