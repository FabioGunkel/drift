package com.gunkel.android.drift.feature.map.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.gunkel.android.drift.core.domain.models.Location
import com.gunkel.android.drift.feature.map.ui.viewmodels.MapViewModel
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: MapViewModel = mockk(relaxed = true)

    @Test
    fun driftButton_shouldTriggerViewModel() {
        // Given
        val locationSlot = slot<Location>()
        composeTestRule.setContent {
            MapScreen(viewModel = viewModel)
        }

        // When
        composeTestRule.onNodeWithTag("drift_button").performClick()

        // Then
        verify { viewModel.onDriftClicked(capture(locationSlot)) }
    }
}
