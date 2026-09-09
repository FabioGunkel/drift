package com.gunkel.android.drift.feature.map.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.components.DriftToggle
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.ui.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToIgnored: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val categorySettings by viewModel.categorySettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Categorias",
                    style = Affectus.typography.titleMedium,
                    color = Affectus.colors.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(categorySettings) { setting ->
                CategorySettingItem(
                    name = mapCategoryToDisplay(setting.categoryName),
                    isEnabled = setting.isEnabled,
                    onToggle = { viewModel.onCategoryToggle(setting.categoryName, it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Affectus.colors.onBackground.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SettingsMenuItem(
                    title = "Locais Ignorados",
                    onClick = onNavigateToIgnored
                )
            }
        }
    }
}

@Composable
fun CategorySettingItem(
    name: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
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
            Text(
                text = name,
                style = Affectus.typography.bodyLarge,
                color = Affectus.colors.onBackground
            )
            DriftToggle(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun SettingsMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Text(
                text = title,
                style = Affectus.typography.bodyLarge,
                color = Affectus.colors.onBackground
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Affectus.colors.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

private fun mapCategoryToDisplay(name: String): String {
    return when (name) {
        "MUSEUM" -> "Museus"
        "PARK" -> "Parques e Natureza"
        "TOURIST_ATTRACTION" -> "Atrações Turísticas"
        "HISTORIC_SITE" -> "Locais Históricos"
        "RESTAURANT" -> "Restaurantes e Cafés"
        else -> name
    }
}
