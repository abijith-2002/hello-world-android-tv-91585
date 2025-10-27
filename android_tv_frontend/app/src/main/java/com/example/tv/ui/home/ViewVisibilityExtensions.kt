package com.example.tv.ui.home

import android.view.View

/**
 * PUBLIC_INTERFACE
 * Simple extensions to toggle view visibility safely.
 *
 * Intended usage:
 * - Used by HomeActivity to control per-section inline loaders and empty-state labels.
 * - Can be used across UI to avoid null-check boilerplate when toggling visibility.
 *
 * Functions:
 * - show(): sets visibility to VISIBLE
 * - hide(): sets visibility to GONE
 */
fun View?.show() { this?.visibility = View.VISIBLE }
fun View?.hide() { this?.visibility = View.GONE }
