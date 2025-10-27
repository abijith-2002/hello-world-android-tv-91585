package com.example.tv.ui.home

import android.view.View

/**
 * PUBLIC_INTERFACE
 * show
 * Makes the view visible.
 */
fun View.show() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

/**
 * PUBLIC_INTERFACE
 * hide
 * Hides the view (GONE).
 */
fun View.hide() {
    if (visibility != View.GONE) visibility = View.GONE
}
