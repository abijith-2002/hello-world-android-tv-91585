package com.example.tv.data

import com.example.tv.data.api.ApiService
import com.example.tv.data.api.ContentItem
import com.example.tv.data.api.HomeCategory

/**
 * PUBLIC_INTERFACE
 * Repository that wraps the ApiService and provides methods to fetch items per home category.
 * Exposes safeFetch that returns Result<List<ContentItem>> for graceful error handling.
 */
class CategoryRepository(
    private val api: ApiService = ApiService.create()
) {

    /**
     * PUBLIC_INTERFACE
     * Fetch items for a specific HomeCategory.
     * @param category HomeCategory enum value
     * @return Result<List<ContentItem>> either success with list or failure with exception
     */
    suspend fun fetchCategory(category: HomeCategory): Result<List<ContentItem>> {
        return safeFetch { api.getCategory(category.path) }
    }

    private inline fun <T> safeFetch(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
