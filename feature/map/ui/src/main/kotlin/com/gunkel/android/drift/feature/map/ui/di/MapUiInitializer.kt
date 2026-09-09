package com.gunkel.android.drift.feature.map.ui.di

import android.content.Context
import androidx.startup.Initializer
import com.gunkel.android.drift.feature.map.domain.di.MapDomainInitializer
import org.koin.core.context.loadKoinModules

class MapUiInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(mapUiModule)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(MapDomainInitializer::class.java)
    }
}
