package com.example.tv.ui.home

import android.content.Context
import coil.Coil

/**
 * PUBLIC_INTERFACE
 * ImageCacheUtils
 * Utilities to manage image caches for verification/testing scenarios.
 * Provides a method to clear Coil's memory and disk caches to simulate a first-run state.
 *
 * Note:
 * - Not invoked automatically in production flows.
 * - Call from a debug menu or test harness if you need to validate "fresh load" behavior.
 */
object ImageCacheUtils {

    // PUBLIC_INTERFACE
    /**
     * Clears Coil memory and disk caches.
     * @param context Android context used by Coil's image loader
     */
    suspend fun clearAllCoilCaches(context: Context) {
        val imageLoader = Coil.imageLoader(context)
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }
}
