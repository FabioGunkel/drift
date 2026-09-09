package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateDriftPathUseCaseTest {

    private val repository: DriftRepository = mockk()
    private val useCase = CalculateDriftPathUseCase(repository)

    @Test
    fun `invoke should return weighted ranking path with correct counts`() = runTest {
        // Given
        val mapCenter = Location(0.0, 0.0)
        
        // Mock responses
        val landmark = Place(
            id = "1",
            name = "Landmark",
            location = Location(0.001, 0.001),
            type = PlaceType.HISTORIC_SITE,
            userRatingsTotal = 5000,
            rating = 5.0
        )
        val restaurant = Place(
            id = "2",
            name = "Restaurant",
            location = Location(0.0005, 0.0005),
            type = PlaceType.RESTAURANT,
            userRatingsTotal = 100,
            rating = 4.0
        )
        val other = Place(
            id = "3",
            name = "Other",
            location = Location(0.0001, 0.0001),
            type = PlaceType.OTHER,
            userRatingsTotal = 10,
            rating = 3.0
        )

        coEvery { repository.getIgnoredIds() } returns emptyList()
        // Landmarks call returns landmark and other
        coEvery { repository.getNearbyPlaces(any(), any(), 1000, match { it.contains("historical_place") }) } returns DataState.Success(listOf(landmark, other))
        // Restaurants call returns restaurant
        coEvery { repository.getNearbyPlaces(any(), any(), 1000, match { it.contains("restaurant") }) } returns DataState.Success(listOf(restaurant))

        // When
        val result = useCase(mapCenter, mapCenter, 1000)

        // Then
        assertTrue(result is DataState.Success)
        val path = (result as DataState.Success).data
        
        // Should only have landmark and restaurant. "Other" should be filtered out.
        assertEquals(2, path.size)
        assertTrue(path.any { it.type == PlaceType.HISTORIC_SITE })
        assertTrue(path.any { it.type == PlaceType.RESTAURANT })
    }

    @Test
    fun `invoke should filter out ignored places`() = runTest {
        // Given
        val mapCenter = Location(0.0, 0.0)
        val ignoredPlace = Place(id = "ignored", name = "Ignored", location = Location(0.001, 0.001), type = PlaceType.MUSEUM, userRatingsTotal = 100, rating = 4.0)
        val validPlace = Place(id = "valid", name = "Valid", location = Location(0.002, 0.002), type = PlaceType.MUSEUM, userRatingsTotal = 100, rating = 4.0)

        coEvery { repository.getIgnoredIds() } returns listOf("ignored")
        coEvery { repository.getNearbyPlaces(any(), any(), any(), any()) } returns DataState.Success(listOf(ignoredPlace, validPlace))

        // When
        val result = useCase(mapCenter, null, 1000)

        // Then
        assertTrue(result is DataState.Success)
        val path = (result as DataState.Success).data
        assertEquals(1, path.size)
        assertEquals("valid", path[0].id)
    }

    @Test
    fun `score calculation should favor closer places and reflect in path`() = runTest {
        val mapCenter = Location(0.0, 0.0)
        
        // Mock responses: 13 landmarks
        val places = (1..13).map { i ->
            Place(
                id = i.toString(),
                name = "Place $i",
                location = Location(0.0001 * i, 0.0001 * i),
                type = PlaceType.HISTORIC_SITE,
                userRatingsTotal = 100,
                rating = 4.0
            )
        }

        coEvery { repository.getIgnoredIds() } returns emptyList()
        coEvery { repository.getNearbyPlaces(any(), any(), any(), any()) } returns DataState.Success(places)

        val result = useCase(mapCenter, mapCenter, 1000)
        val path = (result as DataState.Success).data
        
        // Closest should be first due to nearest neighbor + 2-opt
        assertEquals("1", path[0].id)
    }

    @Test
    fun `ranking should favor close local spot over far popular spot due to popularity cap`() = runTest {
        val mapCenter = Location(0.0, 0.0)
        
        // Popular but far (1000m)
        val popularFar = Place(
            id = "popular",
            name = "Beco do Batman",
            location = Location(0.009, 0.009), // Approx 1000m
            type = PlaceType.TOURIST_ATTRACTION,
            userRatingsTotal = 33000,
            rating = 4.8
        )
        
        // Local gem and close (100m)
        val localClose = Place(
            id = "local",
            name = "Benedito Calixto",
            location = Location(0.0009, 0.0009), // Approx 100m
            type = PlaceType.TOURIST_ATTRACTION,
            userRatingsTotal = 50,
            rating = 4.7
        )

        coEvery { repository.getIgnoredIds() } returns emptyList()
        coEvery { repository.getNearbyPlaces(any(), any(), any(), any()) } returns DataState.Success(listOf(popularFar, localClose))

        val result = useCase(mapCenter, mapCenter, 1000)
        val path = (result as DataState.Success).data
        
        assertEquals("local", path[0].id)
    }

    @Test
    fun `invoke should use provided searchCenter in repository calls`() = runTest {
        val mapCenter = Location(10.0, 20.0)
        val customRadius = 2500
        
        coEvery { repository.getIgnoredIds() } returns emptyList()
        coEvery { repository.getNearbyPlaces(mapCenter.latitude, mapCenter.longitude, customRadius, any()) } returns DataState.Success(emptyList())

        useCase(mapCenter, null, customRadius)

        io.mockk.coVerify {
            repository.getNearbyPlaces(mapCenter.latitude, mapCenter.longitude, customRadius, any())
        }
    }

    @Test
    fun `when startLocation is null should optimize path from searchCenter but not include it`() = runTest {
        val mapCenter = Location(0.0, 0.0)
        val farPlace = Place(id = "far", name = "Far", location = Location(0.002, 0.002), type = PlaceType.PARK, userRatingsTotal = 100, rating = 4.0)
        val nearPlace = Place(id = "near", name = "Near", location = Location(0.001, 0.001), type = PlaceType.PARK, userRatingsTotal = 100, rating = 4.0)

        coEvery { repository.getIgnoredIds() } returns emptyList()
        coEvery { repository.getNearbyPlaces(any(), any(), any(), any()) } returns DataState.Success(listOf(farPlace, nearPlace))

        // When startLocation is null, it should still sort by proximity to mapCenter
        val result = useCase(mapCenter, null, 1000)
        val path = (result as DataState.Success).data

        assertEquals("near", path[0].id)
        assertEquals("far", path[1].id)
    }
}
