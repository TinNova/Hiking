//package com.tinnovakovic.hiking.domain
//
//import android.location.Location
//import com.tinnovakovic.hiking.data.photo.FlickrPhotos
//import com.tinnovakovic.hiking.data.photo.FlickrRepo
//import com.tinnovakovic.hiking.data.photo.HikingPhoto
//import com.tinnovakovic.hiking.data.photo.FlickrDataInteractor
//import io.mockk.coEvery
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.Assertions.*
//
////class GetPhotoFromLocationUseCaseTest {
////
////    private val flickrRepo: FlickrRepo = mockk(relaxed = true)
////    private val mapFlickrPhotoToHikingPhotosUseCase: FlickrDataInteractor =
////        mockk(relaxed = true)
////
////    private val sut: GetPhotoFromLocationUseCase = GetPhotoFromLocationUseCase(
////        flickrRepo,
////        mapFlickrPhotoToHikingPhotosUseCase
////    )
////
////    @Test
////    fun `GIVEN existingHikingPhotos is empty and fetchFlickrPhoto returns two photos, WHEN execute, THEN return first HikingPhoto only`() =
////        runTest {
////            //GIVEN
////            val location = mockk<Location>()
////            val flickrPhotos = mockk<FlickrPhotos>()
////            val hikingPhotos = listOf(
////                HikingPhoto(PHOTO1),
////                HikingPhoto(PHOTO2)
////            )
////
////            coEvery { flickrRepo.fetchFlickrPhoto(location) } returns flickrPhotos
////            every { mapFlickrPhotoToHikingPhotosUseCase.execute(flickrPhotos) } returns hikingPhotos
////
////            //WHEN
////            val result = sut.execute(mutableSetOf(), location)
////
////            //THEN
////            assertEquals(
////                hikingPhotos.first().photo,
////                result.first().photo
////            )
////
////            assertEquals(
////                1,
////                result.size
////            )
////        }
////
////    @Test
////    fun `GIVEN existingHikingPhotos contains photo1 and fetchFlickrPhoto returns photo1 and photo2, WHEN execute, THEN return photo2`() =
////        runTest {
////            //GIVEN
////            val existingHikingPhotos = setOf(
////                HikingPhoto(PHOTO1),
////            )
////
////            val location = mockk<Location>()
////            val flickrPhotos = mockk<FlickrPhotos>()
////            val hikingPhotos = listOf(
////                HikingPhoto(PHOTO1), //ignored because it exists in existingHikingPhotos
////                HikingPhoto(PHOTO2) //this one is added to existingHikingPhotos and returned
////            )
////
////            coEvery { flickrRepo.fetchFlickrPhoto(location) } returns flickrPhotos
////            every { mapFlickrPhotoToHikingPhotosUseCase.execute(flickrPhotos) } returns hikingPhotos
////
////            //WHEN
////            val result = sut.execute(existingHikingPhotos, location)
////
////            //THEN
////            assertEquals(
////                HikingPhoto(PHOTO1).photo,
////                result.first().photo
////            )
////            assertEquals(
////                HikingPhoto(PHOTO2).photo,
////                result.last().photo
////            )
////        }
////
////    @Test
////    fun `GIVEN existingHikingPhotos contains photo1 and fetchFlickrPhoto returns null, WHEN execute, THEN return only photo1`() =
////        runTest {
////            //GIVEN
////            val existingHikingPhotos = setOf(
////                HikingPhoto(PHOTO1),
////            )
////
////            val location = mockk<Location>()
////            val flickrPhotos = null
////
////            coEvery { flickrRepo.fetchFlickrPhoto(location) } returns flickrPhotos
////
////            //WHEN
////            val result = sut.execute(existingHikingPhotos, location)
////
////            //THEN
////            assertEquals(
////                HikingPhoto(PHOTO1).photo,
////                result.first().photo
////            )
////
////            assertEquals(
////                1,
////                result.size
////            )
////            verify(exactly = 0) {
////                mapFlickrPhotoToHikingPhotosUseCase.execute(any())
////            }
////        }
////
////    companion object {
////        const val PHOTO1 = "photo1"
////        const val PHOTO2 = "photo2"
////    }
////}
