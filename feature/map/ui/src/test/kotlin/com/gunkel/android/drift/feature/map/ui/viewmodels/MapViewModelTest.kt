package com.gunkel.android.drift.feature.map.ui.viewmodels

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.feature.map.domain.models.Place
import com.gunkel.android.drift.feature.map.domain.models.PlaceType
import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase
import com.gunkel.android.drift.core.domain.models.Location
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MapViewModel(getNearbyPlacesUseCase)
    }

    @Test
    fun `fetchPlaces success should update uiState`() = runTest {
        // Given
        val places = listOf(Place("1", "Test", Location(0.0, 0.0), type = PlaceType.OTHER))
        coEvery { getNearbyPlacesUseCase(any(), any(), any()) } returns DataState.Success(places)

        // When
        viewModel.fetchPlaces(0.0, 0.0)
        advanceUntilIdle()

        // Then
        assertEquals(DataState.Success(places), viewModel.uiState.value)
    }
}
