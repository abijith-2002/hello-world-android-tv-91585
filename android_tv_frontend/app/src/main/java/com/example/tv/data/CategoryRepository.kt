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
 * Fetches from the network and returns an empty list on failure. No poster overrides are injected.
 */
class CategoryRepository(
    private val api: ApiService = ApiService.create()
) {

    /**
     * PUBLIC_INTERFACE
     * Fetch items for a specific HomeCategory.
     * Attempts a network call to the corresponding endpoint; on failure returns an empty list.
     * Posters are taken strictly from the API's poster field (may be null/blank).
     * @param category HomeCategory enum value
     * @return Result<List<ContentItem>> success with list or success with empty list on failure
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
            // Log error and gracefully return empty list; UI will handle placeholders for null/blank posters.
            Log.e("CategoryRepository", "Failed to fetch ${category.name}: ${t.message}", t)
            Result.success(emptyList())
        }
    }
}
