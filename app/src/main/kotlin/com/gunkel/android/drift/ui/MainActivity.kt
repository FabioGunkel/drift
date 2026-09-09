package com.gunkel.android.drift.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.ui.screens.IgnoredPlacesScreen
import com.gunkel.android.drift.feature.map.ui.screens.MapScreen
import com.gunkel.android.drift.feature.map.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Affectus {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "map") {
                    composable("map") {
                        MapScreen(
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onBackClick = { navController.popBackStack() },
                            onNavigateToIgnored = { navController.navigate("ignored") }
                        )
                    }
                    composable("ignored") {
                        IgnoredPlacesScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
