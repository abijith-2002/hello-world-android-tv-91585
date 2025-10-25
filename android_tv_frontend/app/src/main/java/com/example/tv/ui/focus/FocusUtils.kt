package com.example.tv.ui.focus

import android.view.View
import androidx.annotation.IdRes

/**
 * PUBLIC_INTERFACE
 * FocusUtils centralizes focus movement helpers for the TV app.
 */
object FocusUtils {
    /**
     * PUBLIC_INTERFACE
     * Requests focus for the top menu's first button if present in the current hierarchy.
     *
     * @param root the root view to search from
     * @param menuFirstButtonId the id of the first menu button (e.g., R.id.menuHome)
     * @return true if focus was requested successfully, false otherwise
     */
    fun focusTopMenu(root: View, @IdRes menuFirstButtonId: Int): Boolean {
        val target = root.findViewById<View>(menuFirstButtonId)
        return target?.isFocusable == true && target.requestFocus()
    }
}
