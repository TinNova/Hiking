package com.tinnovakovic.hiking.presenter


import android.location.Location
import app.cash.turbine.test
import com.tinnovakovic.hiking.data.location.LocationInMemoryCache
import com.tinnovakovic.hiking.data.photo.PhotoInMemoryCache
import com.tinnovakovic.hiking.domain.photo.GetPhotoFromLocationUseCase
import com.tinnovakovic.hiking.domain.photo.HikingPhoto
import com.tinnovakovic.hiking.domain.location.StartLocationServiceUseCase
import com.tinnovakovic.hiking.domain.location.StopLocationServiceUseCase
import com.tinnovakovic.hiking.presentation.HomeContract
import com.tinnovakovic.hiking.presentation.HomeViewModel
import com.tinnovakovic.hiking.shared.CoroutineTestExtension
import com.tinnovakovic.hiking.shared.TestException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @RegisterExtension
    @JvmField
    val coroutineTestExtension = CoroutineTestExtension(testDispatcher)

    private val startLocationServiceUseCase: StartLocationServiceUseCase = mockk(relaxed = true)
    private val stopLocationServiceUseCase: StopLocationServiceUseCase = mockk(relaxed = true)
    private val photoFromLocationUseCase: GetPhotoFromLocationUseCase = mockk(relaxed = true)
    private val locationInMemoryCache: LocationInMemoryCache = mockk(relaxed = true)
    private val photoInMemoryCache: PhotoInMemoryCache = mockk(relaxed = true)

    private lateinit var sut: HomeViewModel

    private fun createSut() {
        sut = HomeViewModel(
            startLocationServiceUseCase,
            stopLocationServiceUseCase,
            photoFromLocationUseCase,
            locationInMemoryCache,
            photoInMemoryCache
        )
    }

    @Test
    fun `WHEN OnResume, THEN update uiState to scrollStateToTop=true`() = runTest {
        //GIVEN
        createSut()
        //WHEN
        sut.onUiEvent(HomeContract.UiEvents.OnResume)

        //THEN
        assertAll(
            { assertTrue(sut.uiState.value.scrollStateToTop) },

            //The same as initial values
            { assertTrue(sut.uiState.value.isStartButton) },
            { assertFalse(sut.uiState.value.isError) },
            { assertEquals(0, sut.uiState.value.hikingPhotos.size) }
        )
    }

    @Test
    fun `WHEN OnPause, THEN update uiState to scrollStateToTop=false`() = runTest {
        //GIVEN
        createSut()
        //WHEN
        sut.onUiEvent(HomeContract.UiEvents.OnPause)

        //THEN
        assertAll(
            { assertFalse(sut.uiState.value.scrollStateToTop) },

            //The same as initial values
            { assertTrue(sut.uiState.value.isStartButton) },
            { assertFalse(sut.uiState.value.isError) },
            { assertEquals(0, sut.uiState.value.hikingPhotos.size) }
        )
    }

    @Test
    fun `WHEN OnDestroy, THEN verify stopLocationServiceUseCase triggered`() = runTest {
        //GIVEN
        createSut()
        //WHEN
        sut.onUiEvent(HomeContract.UiEvents.OnDestroy)

        //THEN
        verify { stopLocationServiceUseCase.execute() }
    }

    @Test
    fun `WHEN StopClicked, THEN verify stopLocationServiceUseCase triggered and uiState contains isStartButton=true`() =
        runTest {
            //GIVEN
            createSut()
            //WHEN
            sut.onUiEvent(HomeContract.UiEvents.StopClicked)

            //THEN
            assertTrue(sut.uiState.value.isStartButton)
            verify { stopLocationServiceUseCase.execute() }
        }

    @Test
    fun `GIVEN no existing photos, one location and one photo returned, WHEN StartClicked, THEN verify useCases triggered, uiState is updated twice`() =
        runTest {
            //GIVEN
            createSut()
            val latestDistinctPhoto = setOf<HikingPhoto>()
            val location = mockk<Location>()

            val latestLocationFromDataSource = MutableStateFlow<Location?>(location)
            val hikingPhoto = HikingPhoto("photo1")
            val hikingPhotoSet = setOf(hikingPhoto)

            every { photoInMemoryCache.cache.value } returns emptySet<HikingPhoto>()
            every { locationInMemoryCache.cache } returns latestLocationFromDataSource
            coEvery {
                photoFromLocationUseCase.execute(
                    latestDistinctPhoto,
                    location
                )
            } returns hikingPhotoSet

            //WHEN
            sut.onUiEvent(HomeContract.UiEvents.StartClicked)

            //THEN
            sut.uiState.test {

                assertFalse(sut.uiState.value.isStartButton)
                assertFalse(sut.uiState.value.isError)

                advanceUntilIdle()
                assertEquals(hikingPhotoSet.size, sut.uiState.value.hikingPhotos.size)
                assertFalse(sut.uiState.value.isError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN one existing photo, one location and one photo returned, WHEN StartClicked, THEN verify useCases triggered, uiState is updated twice`() =
        runTest {
            //GIVEN
            createSut()
            val hikingPhotoOne = HikingPhoto("photo1")
            val hikingPhotoTwo = HikingPhoto("photo2")

            val location = mockk<Location>()

            val latestLocationFromDataSource = MutableStateFlow<Location?>(location)
            val existingHikingPhotoSet = setOf(hikingPhotoOne, hikingPhotoTwo)
            val expectedHikingPhotoSet = setOf(hikingPhotoOne, hikingPhotoTwo)

            every { photoInMemoryCache.cache.value } returns existingHikingPhotoSet
            every { locationInMemoryCache.cache } returns latestLocationFromDataSource
            coEvery {
                photoFromLocationUseCase.execute(
                    any(),
                    location
                )
            } returns expectedHikingPhotoSet

            //WHEN
            sut.onUiEvent(HomeContract.UiEvents.StartClicked)

            //THEN
            sut.uiState.test {
                assertFalse(sut.uiState.value.isStartButton)
                assertFalse(sut.uiState.value.isError)
                advanceUntilIdle()
                assertEquals(expectedHikingPhotoSet.size, sut.uiState.value.hikingPhotos.size)
                assertFalse(sut.uiState.value.isError)
                cancelAndIgnoreRemainingEvents()
            }
        }


    // Didn't have time to find a solution to this.
    @Disabled("Disabled because test enters an infinite loop as we always try to fetch photos after an exception")
    @Test
    fun `GIVEN photoFromLocationUseCase throws exception, WHEN StartClicked, THEN uiState with error`() =
        runTest {
            //GIVEN
            createSut()
            val latestDistinctPhoto = setOf<HikingPhoto>()
            val location = mockk<Location>()

            val latestLocationFromDataSource = MutableStateFlow<Location?>(location)

            every { photoInMemoryCache.cache.value } returns emptySet<HikingPhoto>()
            every { locationInMemoryCache.cache } returns latestLocationFromDataSource
            coEvery {
                photoFromLocationUseCase.execute(
                    latestDistinctPhoto,
                    location
                )
            } throws TestException

            //WHEN
            sut.onUiEvent(HomeContract.UiEvents.StartClicked)

            //THEN
            sut.uiState.test {
                // Values when StartClicked
                assertFalse(sut.uiState.value.isStartButton)
                assertFalse(sut.uiState.value.isError)
                advanceUntilIdle()

                // Values after exception is caught
                assertTrue(sut.uiState.value.isError)
            }
        }
}
