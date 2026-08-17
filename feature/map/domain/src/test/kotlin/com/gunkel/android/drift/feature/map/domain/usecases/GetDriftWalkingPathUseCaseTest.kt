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

class GetDriftWalkingPathUseCaseTest {

    private val calculateStopsUseCase: CalculateDriftPathUseCase = mockk()
    private val repository: DriftRepository = mockk()
    private val useCase = GetDriftWalkingPathUseCase(calculateStopsUseCase, repository)

    private val mapCenter = Location(-23.5616, -46.6866)
    private val place1 = Place(id = "1", name = "Place 1", location = Location(-23.5617, -46.6867), type = PlaceType.PARK, userRatingsTotal = 100, rating = 4.5)
    private val place2 = Place(id = "2", name = "Place 2", location = Location(-23.5618, -46.6868), type = PlaceType.MUSEUM, userRatingsTotal = 200, rating = 4.8)

    @Test
    fun `when user is NEAR center should use user location as origin`() = runTest {
        // Given
        val userLocation = Location(-23.56161, -46.68661) // Very close to center
        val radius = 1000
        
        coEvery { calculateStopsUseCase(mapCenter, userLocation, radius) } returns DataState.Success(listOf(place1, place2))
        coEvery { repository.getPathDirections(any(), any(), any()) } returns DataState.Success("polyline")

        // When
        val result = useCase(mapCenter, userLocation, radius)

        // Then
        assertTrue(result is DataState.Success)
        // Verify repository call uses userLocation as origin
        val expectedOrigin = "${userLocation.latitude},${userLocation.longitude}"
        io.mockk.coVerify {
            repository.getPathDirections(expectedOrigin, any(), any())
        }
    }

    @Test
    fun `when user is FAR from center should use first place as origin`() = runTest {
        // Given
        val userLocation = Location(0.0, 0.0) // Very far from mapCenter
        val radius = 1000
        
        coEvery { calculateStopsUseCase(mapCenter, null, radius) } returns DataState.Success(listOf(place1, place2))
        coEvery { repository.getPathDirections(any(), any(), any()) } returns DataState.Success("polyline")

        // When
        val result = useCase(mapCenter, userLocation, radius)

        // Then
        assertTrue(result is DataState.Success)
        // Verify repository call uses first place as origin
        val expectedOrigin = "${place1.location.latitude},${place1.location.longitude}"
        io.mockk.coVerify {
            repository.getPathDirections(expectedOrigin, any(), any())
        }
    }

    @Test
    fun `when userLocation is null should use first place as origin`() = runTest {
        // Given
        val radius = 1000
        
        coEvery { calculateStopsUseCase(mapCenter, null, radius) } returns DataState.Success(listOf(place1, place2))
        coEvery { repository.getPathDirections(any(), any(), any()) } returns DataState.Success("polyline")

        // When
        val result = useCase(mapCenter, null, radius)

        // Then
        assertTrue(result is DataState.Success)
        val expectedOrigin = "${place1.location.latitude},${place1.location.longitude}"
        io.mockk.coVerify {
            repository.getPathDirections(expectedOrigin, any(), any())
        }
    }
}
