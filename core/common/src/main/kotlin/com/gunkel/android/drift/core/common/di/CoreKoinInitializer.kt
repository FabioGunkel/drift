package com.gunkel.android.drift.core.common.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.google.android.libraries.places.api.Places
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class CoreKoinInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        // Initialize Google Places SDK with New API enabled
        if (!Places.isInitialized()) {
            val apiKey = getApiKey(context)
            if (apiKey != null) {
                Places.initializeWithNewPlacesApiEnabled(context, apiKey)
            }
        }
        
        val placesModule = module {
            single { Places.createClient(context) }
            single(qualifier = org.koin.core.qualifier.named("MAPS_API_KEY")) { 
                getApiKey(context) ?: "" 
            }
        }

        loadKoinModules(placesModule)
    }

    private fun getApiKey(context: Context): String? {
        return try {
            val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            bundle.getString("com.google.android.geo.API_KEY")
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(
            org.koin.androix.startup.KoinInitializer::class.java
        )
    }
}
