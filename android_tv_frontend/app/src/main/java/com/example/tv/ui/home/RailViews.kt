package com.example.tv.ui.home

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.HorizontalScrollView
import androidx.annotation.IdRes
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * Helper container to access common rail child views safely.
 */
data class RailViews(
    val root: View,
    val title: TextView?,
    val row: LinearLayout?,
    val scroll: HorizontalScrollView?,
    val loadingChip: View?,
    val loadingFallback: ProgressBar?,
    val emptyState: View?
) {
    companion object {
        // PUBLIC_INTERFACE
        fun from(root: View): RailViews {
            return RailViews(
                root = root,
                title = root.findViewById(R.id.railTitle),
                row = root.findViewById(R.id.railRow),
                scroll = root.findViewById(R.id.railScroll),
                loadingChip = root.findViewById(R.id.railLoadingChip),
                loadingFallback = root.findViewById(R.id.railTinyProgressFallback),
                emptyState = root.findViewById(R.id.railEmptyState)
            )
        }
    }
}
