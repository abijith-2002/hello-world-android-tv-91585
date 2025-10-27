package com.example.tv.data.api.dto

import com.example.tv.data.api.ContentItem

/**
 * PUBLIC_INTERFACE
 * Map ShowDto into UI model ContentItem.
 */
fun ShowDto.toContentItem(): ContentItem {
    return ContentItem(
        name = this.name ?: "Unknown",
        poster = this.poster
    )
}
