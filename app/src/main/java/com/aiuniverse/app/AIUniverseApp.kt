package com.aiuniverse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application entry point for AI Universe.
 * Initializes Hilt dependency injection, global loggers, and app configurations.
 */
@HiltAndroidApp
class AIUniverseApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
