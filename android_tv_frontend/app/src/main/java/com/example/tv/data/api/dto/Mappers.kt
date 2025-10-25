package com.example.tv.data.api.dto

import com.example.tv.data.api.ContentItem

/**
 * PUBLIC_INTERFACE
 * Extension mappers for converting API DTOs to domain models used by the UI.
 */
// PUBLIC_INTERFACE
fun ShowDto.toContentItem(): ContentItem = ContentItem(
    id = this.id,
    name = this.name,
    poster = this.poster
)
