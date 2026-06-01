package com.gunkel.android.drift.feature.map.ui.viewmodels

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.data.models.DriftPath
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.domain.usecases.GetDriftWalkingPathUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel
    private val getDriftWalkingPathUseCase: GetDriftWalkingPathUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MapViewModel(getDriftWalkingPathUseCase)
    }

    @Test
    fun `onDriftClicked success should update uiState to PathFound`() = runTest {
        // Given
        val location = Location(0.0, 0.0)
        val path = DriftPath(
            stops = listOf(Place("1", "Test", Location(0.1, 0.1), type = PlaceType.OTHER)),
            polylinePoints = "abc"
        )
        coEvery { getDriftWalkingPathUseCase(location) } returns DataState.Success(path)

        // When
        viewModel.onDriftClicked(location)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is DriftUiState.PathFound)
        val state = viewModel.uiState.value as DriftUiState.PathFound
        assertEquals(path.stops, state.stops)
        assertEquals(path.polylinePoints, state.polylinePoints)
    }
}
