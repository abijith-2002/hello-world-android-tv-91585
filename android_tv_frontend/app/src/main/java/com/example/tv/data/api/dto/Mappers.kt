package com.example.tv.data.api.dto

import com.example.tv.data.api.ContentItem

/**
 * PUBLIC_INTERFACE
 * Extension mappers for converting API DTOs to domain models used by the UI.
 * Posters are taken strictly from the API's poster field. No fallback/sample URLs are injected here.
 * The UI layer is responsible for showing a static placeholder when poster is null or blank.
 */
// PUBLIC_INTERFACE
fun ShowDto.toContentItem(): ContentItem = ContentItem(
    id = this.id,
    name = this.name,
    poster = this.poster // nullable; UI handles placeholder for null/blank
)
