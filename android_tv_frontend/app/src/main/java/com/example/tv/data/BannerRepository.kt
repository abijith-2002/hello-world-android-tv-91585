package com.example.tv.data

import android.util.Log
import com.example.tv.data.api.ApiService

/**
 * PUBLIC_INTERFACE
 * BannerRepository
 * Fetches hero banner image URLs from the backend and provides a resilient API with fallback.
 */
class BannerRepository(
    private val api: ApiService = ApiService.create()
) {

    // PUBLIC_INTERFACE
    /**
     * Fetch the list of banner image URLs.
     * Returns success with a non-empty list or success with an empty list if network fails.
     */
    suspend fun fetchBanners(): Result<List<String>> {
        return try {
            val resp = api.getBanners()
            val list = resp.banners.filter { it.isNotBlank() }
            Result.success(list)
        } catch (t: Throwable) {
            Log.e("BannerRepository", "Failed to load banners: ${t.message}", t)
            Result.success(emptyList())
        }
    }
}
