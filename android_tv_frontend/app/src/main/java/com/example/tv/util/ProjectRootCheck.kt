package com.example.tv.util

import android.content.Context
import android.util.Log
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * ProjectRootCheck
 * Utility to verify this module is the actual Android Gradle root at runtime
 * and that Content Info scoped dimens are resolvable.
 *
 * - Accepts a Context.
 * - Returns true if key resources resolve; false otherwise (and logs).
 */
object ProjectRootCheck {
    private const val TAG = "ProjectRootCheck"

    // PUBLIC_INTERFACE
    fun verify(context: Context): Boolean {
        try {
            // touch a few resources to ensure merge worked (ci_* dimens and a string)
            val dimenId = R.dimen.ci_panel_width
            val px = context.resources.getDimension(dimenId)
            val title = context.getString(R.string.content_info_title)
            Log.d(TAG, "Resolved ci_panel_width=$px, content_info_title=$title")
            return px > 0
        } catch (t: Throwable) {
            Log.e(TAG, "Resource verification failed", t)
            return false
        }
    }
}
