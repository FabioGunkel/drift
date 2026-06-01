package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.domain.models.Location
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
    fun `invoke should return optimized path with prioritized places`() = runTest {
        // Given
        val userLoc = Location(0.0, 0.0)
        val mockPlaces = listOf(
            Place("1", "Museum", Location(0.01, 0.01), type = PlaceType.MUSEUM),
            Place("2", "Park", Location(0.02, 0.02), type = PlaceType.PARK),
            Place("3", "Historic", Location(0.005, 0.005), type = PlaceType.HISTORIC_SITE)
        )
        coEvery { repository.getNearbyPlaces(any(), any(), any()) } returns DataState.Success(mockPlaces)

        // When
        val result = useCase(userLoc, 2000)

        // Then
        assertTrue(result is DataState.Success)
        val path = (result as DataState.Success).data
        // Historic should be first because it's closest and highest priority
        assertEquals("3", path[0].id)
    }

    @Test
    fun `invoke should return error if no places found`() = runTest {
        // Given
        coEvery { repository.getNearbyPlaces(any(), any(), any()) } returns DataState.Success(emptyList())

        // When
        val result = useCase(Location(0.0, 0.0), 2000)

        // Then
        assertTrue(result is DataState.Error)
        assertEquals("No places found nearby", (result as DataState.Error).message)
    }
}
