package com.example.tv.utilities

import android.view.KeyEvent

/**
 * PUBLIC_INTERFACE
 * Dpad
 * Utility constants and helpers for D-Pad navigation.
 */
object Dpad {
    // PUBLIC_INTERFACE
    /** Returns true if the keyCode is a vertical navigation key (UP/DOWN). */
    fun isVertical(keyCode: Int): Boolean =
        keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN

    // PUBLIC_INTERFACE
    /** Returns true if the keyCode is a horizontal navigation key (LEFT/RIGHT). */
    fun isHorizontal(keyCode: Int): Boolean =
        keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
}
