package com.example.tv.ui.home

import android.content.Context
import android.widget.ImageView
import coil.Coil
import coil.imageLoader
import coil.dispose

/**
 * PUBLIC_INTERFACE
 * ImageCacheUtils
 * Utilities to manage image caches and image requests for verification/testing scenarios.
 * - clearAllCoilCaches: simulate a first-run by clearing memory and disk caches.
 * - cancelOngoingRequest: cancel any in-flight request tied to a given ImageView to avoid
 *   content flash or wrong-size reuse when views are rebound.
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

    // PUBLIC_INTERFACE
    /**
     * Cancel any pending coil request associated with this ImageView.
     * Safe to call before starting a new load on the same view.
     * @param imageView The target ImageView whose request should be cancelled
     */
    fun cancelOngoingRequest(imageView: ImageView) {
        // Coil 2 provides an extension to dispose the ImageView's current request
        imageView.dispose()
    }
}
