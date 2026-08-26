package com.gunkel.android.drift.feature.map.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.ui.viewmodels.PlaceDetailsUiState
import com.gunkel.android.drift.feature.map.ui.viewmodels.PlaceDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsBottomSheet(
    place: Place,
    onDismiss: () -> Unit,
    onPlaceIgnored: (String) -> Unit,
    viewModel: PlaceDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(place) {
        viewModel.loadPlaceDetails(place)
    }

    LaunchedEffect(uiState) {
        if (uiState is PlaceDetailsUiState.PlaceIgnored) {
            onPlaceIgnored((uiState as PlaceDetailsUiState.PlaceIgnored).placeName)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Affectus.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is PlaceDetailsUiState.Loading, is PlaceDetailsUiState.Success -> {
                    val displayPlace = if (state is PlaceDetailsUiState.Success) state.place else place
                    
                    PlaceInfoWindowContent(
                        place = displayPlace,
                        showAddIcon = false,
                        showAiSummary = true
                    )
                    
                    if (state is PlaceDetailsUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Affectus.colors.primary,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.ignorePlace(displayPlace) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Affectus.colors.error.copy(alpha = 0.1f),
                            contentColor = Affectus.colors.error
                        ),
                        shape = RoundedCornerShape(Affectus.dimens.radiusM)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ignorar este local")
                    }
                }
                else -> { /* Idle or Ignored - handled by effects or initial display */ }
            }
        }
    }
}
