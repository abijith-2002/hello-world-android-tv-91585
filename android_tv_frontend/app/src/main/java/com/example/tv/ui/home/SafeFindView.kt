package com.example.tv.ui.home

import android.view.View

/**
 * PUBLIC_INTERFACE
 * Safe findViewById helpers that return null instead of throwing.
 */
inline fun <reified T : View> View.safeFind(id: Int): T? = try {
    findViewById<T>(id)
} catch (_: Throwable) {
    null
}
