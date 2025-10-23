package com.example.tv.data.api.dto

/**
 * PUBLIC_INTERFACE
 * Data transfer object for shows returned by the backend endpoints.
 * Fields align with server: { name, poster }
 */
data class ShowDto(
    val name: String,
    val poster: String
)
