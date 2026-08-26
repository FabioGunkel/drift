package com.gunkel.android.drift.feature.map.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import com.gunkel.android.drift.feature.map.ui.viewmodels.IgnoredPlacesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IgnoredPlacesScreen(
    onBackClick: () -> Unit,
    viewModel: IgnoredPlacesViewModel = koinViewModel()
) {
    val ignoredPlaces by viewModel.ignoredPlaces.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locais Ignorados") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Affectus.colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Affectus.colors.background,
                    titleContentColor = Affectus.colors.onBackground
                )
            )
        }
    ) { paddingValues ->
        if (ignoredPlaces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum local ignorado",
                    style = Affectus.typography.bodyLarge,
                    color = Affectus.colors.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(ignoredPlaces, key = { it.id }) { place ->
                    IgnoredPlaceItem(
                        place = place,
                        onRemoveClick = { viewModel.onRemoveClicked(place.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun IgnoredPlaceItem(
    place: IgnoredPlaceEntity,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Affectus.colors.secondary.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.title,
                    style = Affectus.typography.titleMedium,
                    color = Affectus.colors.onBackground
                )
            }
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = Affectus.colors.error
                )
            }
        }
    }
}
