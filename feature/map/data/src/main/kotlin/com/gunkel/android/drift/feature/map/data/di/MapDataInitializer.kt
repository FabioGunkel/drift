package com.gunkel.android.drift.feature.map.data.di

import android.content.Context
import androidx.startup.Initializer
import org.koin.androidx.startup.KoinInitializer
import org.koin.core.context.loadKoinModules

class MapDataInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(mapDataModule)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(KoinInitializer::class.java)
    }
}
