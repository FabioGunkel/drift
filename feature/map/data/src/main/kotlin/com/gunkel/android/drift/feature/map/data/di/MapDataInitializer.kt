package com.gunkel.android.drift.feature.map.data.di

import android.content.Context
import androidx.startup.Initializer
import com.gunkel.android.drift.core.network.di.NetworkInitializer
import org.koin.core.context.loadKoinModules

class MapDataInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(mapDataModule)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(NetworkInitializer::class.java)
    }
}
