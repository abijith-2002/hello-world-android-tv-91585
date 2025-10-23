package com.example.tv

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/**
 * PUBLIC_INTERFACE
 * TVApp
 * Application class for potential future initialization for the Android TV app.
 * Initializes app-wide configuration if needed in future.
 * No parameters and no return value.
 */
class TVApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Ensure vector drawables from resources are supported on all API levels and OEM TV devices.
        // Although build.gradle sets vectorDrawables.useSupportLibrary=true, some devices still require
        // the runtime flag to be explicit to avoid missing image issues with VectorDrawable.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
