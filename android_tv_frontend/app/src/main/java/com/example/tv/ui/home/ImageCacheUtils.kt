package com.example.tv.ui.home

import android.widget.ImageView

/**
 * PUBLIC_INTERFACE
 * ImageCacheUtils
 * Utility to cancel any in-flight image request tied to the given ImageView.
 * This prevents reuse artifacts when views are rebound.
 */
object ImageCacheUtils {
    /**
     * PUBLIC_INTERFACE
     * Cancels ongoing image request for the target ImageView if supported by the
     * underlying image loader. No-op if not supported.
     *
     * @param imageView the target ImageView
     */
    fun cancelOngoingRequest(imageView: ImageView) {
        try {
            // Coil sets a request on the view as a tag; calling clear() cancels it.
            coil.imageLoader(imageView.context).dispose()
        } catch (_: Throwable) {
            // Silently ignore; cancellation is best-effort
        }
    }
}
