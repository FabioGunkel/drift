package com.gunkel.android.drift.feature.map.domain.di

import android.content.Context
import androidx.startup.Initializer
import com.gunkel.android.drift.feature.map.data.di.MapDataInitializer
import org.koin.core.context.loadKoinModules

class MapDomainInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(mapDomainModule)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(MapDataInitializer::class.java)
    }
}
