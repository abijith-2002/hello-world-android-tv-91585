package com.example.tv.data

import com.example.tv.data.api.ApiService
import com.example.tv.data.api.ContentItem
import com.example.tv.data.api.HomeCategory
import com.example.tv.data.api.dto.ShowDto
import com.example.tv.data.api.dto.toContentItem
import android.util.Log

/**
 * PUBLIC_INTERFACE
 * Repository that wraps the ApiService and provides methods to fetch items per home category.
 * Fetches from the network and gracefully falls back to sample data if the request fails.
 */
class CategoryRepository(
    private val api: ApiService = ApiService.create()
) {

    // Simple sample content used when network fails
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
     * Attempts a network call to the corresponding endpoint and falls back to samples upon any error.
     * @param category HomeCategory enum value
     * @return Result<List<ContentItem>> success with list or success with samples on failure
     */
    suspend fun fetchCategory(category: HomeCategory): Result<List<ContentItem>> {
        return try {
            val dtos: List<ShowDto> = when (category) {
                HomeCategory.TRENDING -> api.getTrending()
                HomeCategory.CONTINUE_WATCHING -> api.getContinueWatching()
                HomeCategory.ACTION -> api.getAction()
                HomeCategory.FAMILY -> api.getFamily()
                HomeCategory.COMEDY -> api.getComedy()
                HomeCategory.HORROR -> api.getHorror()
                HomeCategory.DRAMA -> api.getDrama()
            }
            Result.success(dtos.map { it.toContentItem() })
        } catch (t: Throwable) {
            // Log error and gracefully fall back to local samples so the UI remains functional
            Log.e("CategoryRepository", "Failed to fetch ${category.name}: ${t.message}", t)
            Result.success(sampleItemsFor(category))
        }
    }
}
