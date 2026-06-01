package com.gunkel.android.drift.feature.map.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.domain.models.Location
import com.gunkel.android.drift.feature.map.domain.models.Place
import com.gunkel.android.drift.feature.map.domain.models.PlaceType

@Composable
fun MapContent(
    uiState: DataState<List<Place>>,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is DataState.Loading -> {
                    CircularProgressIndicator()
                }
                is DataState.Success -> {
                    Text(text = "Found ${uiState.data.size} places")
                    // Real Map implementation would go here
                }
                is DataState.Error -> {
                    Text(text = "Error: ${uiState.message}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapContentPreview() {
    MapContent(
        uiState = DataState.Success(
            listOf(
                Place("1", "Museum", Location(0.0, 0.0), type = PlaceType.MUSEUM)
            )
        )
    )
}
