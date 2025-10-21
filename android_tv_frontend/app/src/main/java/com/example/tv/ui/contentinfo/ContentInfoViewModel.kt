package com.example.tv.ui.contentinfo

import android.content.Intent

/**
 * PUBLIC_INTERFACE
 * ContentInfoViewModel
 * Lightweight holder of content info for the ContentInfoActivity.
 * This is not Android Architecture ViewModel to keep dependencies minimal;
 * it simply encapsulates data extracted from the Intent.
 */
data class ContentInfoViewModel(
    val title: String,
    val subtitle: String?,
    val description: String?,
    val posterResId: Int?,
    val channelNumber: String?,
    val channelName: String?
) {
    companion object {
        /**
         * PUBLIC_INTERFACE
         * fromIntent
         * Build a ContentInfoViewModel from an Intent that includes the Content Info extras.
         * @param intent Intent containing extras from ContentInfoActivity companion constants.
         * @return ContentInfoViewModel populated with available data.
         */
        fun fromIntent(intent: Intent): ContentInfoViewModel {
            return ContentInfoViewModel(
                title = intent.getStringExtra(ContentInfoActivity.EXTRA_TITLE) ?: "Content",
                subtitle = intent.getStringExtra(ContentInfoActivity.EXTRA_SUBTITLE),
                description = intent.getStringExtra(ContentInfoActivity.EXTRA_DESCRIPTION),
                posterResId = if (intent.hasExtra(ContentInfoActivity.EXTRA_POSTER_RES_ID))
                    intent.getIntExtra(ContentInfoActivity.EXTRA_POSTER_RES_ID, 0).takeIf { it != 0 }
                else null,
                channelNumber = intent.getStringExtra(ContentInfoActivity.EXTRA_CHANNEL_NUMBER),
                channelName = intent.getStringExtra(ContentInfoActivity.EXTRA_CHANNEL_NAME)
            )
        }
    }
}
