package com.example.tv.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * PUBLIC_INTERFACE
 * ContentItem
 * Data model representing a TV content item with all metadata needed for the detail screen.
 * @param title The main title of the content
 * @param subtitle Optional subtitle or original title
 * @param channelNumber The channel number
 * @param channelName The channel name
 * @param duration Duration string (e.g., "2 h 28 min")
 * @param genres Comma-separated genres
 * @param ageRating Age rating (e.g., "+ 16 Años")
 * @param timeSlot Time slot (e.g., "20:00 - 22:20")
 * @param timeLabel Label like "MÁS TARDE", "EN VIVO", etc.
 * @param description Full description text
 * @param imageResId Resource ID of the background image
 */
@Parcelize
data class ContentItem(
    val title: String,
    val subtitle: String = "",
    val channelNumber: String = "242",
    val channelName: String = "TNT",
    val duration: String = "",
    val genres: String = "",
    val ageRating: String = "",
    val timeSlot: String = "",
    val timeLabel: String = "",
    val description: String = "",
    val imageResId: Int = 0
) : Parcelable
