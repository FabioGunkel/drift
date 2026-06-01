package com.gunkel.android.drift

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

class DriftApp : Application(), org.koin.androix.startup.KoinStartup {

    @OptIn(KoinExperimentalAPI::class)
    override fun onKoinStartup() = koinConfiguration {
        androidLogger()
        androidContext(this@DriftApp)
        modules(emptyList()) 
    }
}
