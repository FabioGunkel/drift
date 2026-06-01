package com.gunkel.android.drift.di

import android.content.Context
import androidx.startup.Initializer
import com.gunkel.android.drift.feature.map.data.repositories.MapRepository
import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase

class DependencyGraphInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val mapRepo = MapRepository()
        val getNearbyPlacesUseCase = GetNearbyPlacesUseCase(mapRepo)
        
        AppContainer.mapRepository = mapRepo
        AppContainer.getNearbyPlacesUseCase = getNearbyPlacesUseCase
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
