package com.example.tv.data.api

import com.example.tv.data.api.dto.ShowDto

/**
 * PUBLIC_INTERFACE
 * Minimal ApiService abstraction that provides category endpoints.
 * Implementations can fetch from network; default create() returns a stub with sample data.
 */
interface ApiService {
    // PUBLIC_INTERFACE
    suspend fun getTrending(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getContinueWatching(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getAction(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getDrama(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getHorror(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getFamily(): List<ShowDto>

    // PUBLIC_INTERFACE
    suspend fun getComedy(): List<ShowDto>

    companion object {
        // PUBLIC_INTERFACE
        fun create(): ApiService = StubApiService()
    }
}

/**
 * Simple stub implementation returning empty lists; repository will fallback to samples.
 */
private class StubApiService : ApiService {
    override suspend fun getTrending(): List<ShowDto> = emptyList()
    override suspend fun getContinueWatching(): List<ShowDto> = emptyList()
    override suspend fun getAction(): List<ShowDto> = emptyList()
    override suspend fun getDrama(): List<ShowDto> = emptyList()
    override suspend fun getHorror(): List<ShowDto> = emptyList()
    override suspend fun getFamily(): List<ShowDto> = emptyList()
    override suspend fun getComedy(): List<ShowDto> = emptyList()
}
