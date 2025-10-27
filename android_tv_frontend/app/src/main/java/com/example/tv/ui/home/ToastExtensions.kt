package com.example.tv.ui.home

import android.content.Context
import android.widget.Toast

/**
 * PUBLIC_INTERFACE
 * Shows a short toast safely if context is non-null and message is not blank.
 */
fun Context?.safeToast(message: String?) {
    if (this != null && !message.isNullOrBlank()) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
