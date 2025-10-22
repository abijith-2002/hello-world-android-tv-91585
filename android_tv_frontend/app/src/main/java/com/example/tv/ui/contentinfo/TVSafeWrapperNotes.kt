package com.example.tv.ui.contentinfo

/**
 * PUBLIC_INTERFACE
 * TVSafeWrapperNotes
 * Notes for enabling optional root-level scale on Content Info screen without affecting focus.
 *
 * To apply a temporary scale for QA on low-overscan devices:
 *
 * val wrapper = findViewById<android.view.View>(R.id.tvSafeWrapper)
 * wrapper?.apply {
 *     // Try 0.92f–0.88f as needed
 *     scaleX = 0.92f
 *     scaleY = 0.92f
 *     // Keep children focusable; focus rings and ripples will remain aligned thanks to child-local sizing.
 * }
 *
 * Do not leave non-1.0 scale in production unless absolutely necessary; prefer ci_* dimens tuning first.
 */
object TVSafeWrapperNotes
