package com.example.tv.data

import com.example.tv.BuildConfig
import com.example.tv.data.api.ApiService
import com.example.tv.data.api.ContentItem
import com.example.tv.data.api.HomeCategory

/**
 * PUBLIC_INTERFACE
 * Repository that wraps the ApiService and provides methods to fetch items per home category.
 * Exposes safeFetch that returns Result<List<ContentItem>> for graceful error handling.
 * Includes an offline fallback so the Hello World app functions without a backend.
 */
class CategoryRepository(
    private val api: ApiService = ApiService.create()
) {

    // Determine if we should avoid network usage for this Hello World app
    private val offlineMode: Boolean = run {
        val base = (BuildConfig.API_BASE_URL ?: "").trim()
        base.isBlank() || base.contains("example.com", ignoreCase = true)
    }

    // Simple sample content used when offlineMode is true or when network fails
    private fun sampleItemsFor(category: HomeCategory): List<ContentItem> {
        val baseNames = listOf(
            "Hello World", "Sample Show", "Demo Title",
            "Pilot Episode", "Featurette", "Spotlight"
        )
        return baseNames.mapIndexed { idx, name ->
            // Use null for poster so Coil shows our placeholder/error drawables
            ContentItem(name = "$name • ${category.title} #${idx + 1}", poster = null)
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Fetch items for a specific HomeCategory.
     * - If offline mode is enabled, returns local sample items.
     * - Otherwise, fetches from the API and falls back to sample items on failure.
     * @param category HomeCategory enum value
     * @return Result<List<ContentItem>> either success with list or failure with exception
     */
    suspend fun fetchCategory(category: HomeCategory): Result<List<ContentItem>> {
        if (offlineMode) {
            return Result.success(sampleItemsFor(category))
        }
        // Attempt network fetch, but fall back to samples on error
        val result = safeFetch { api.getCategory(category.path) }
        return result.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.success(sampleItemsFor(category)) } // graceful fallback
        )
    }

    private inline fun <T> safeFetch(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
