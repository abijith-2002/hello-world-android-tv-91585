package com.example.tv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv.data.CategoryRepository
import com.example.tv.data.api.ContentItem
import com.example.tv.data.api.HomeCategory
import com.example.tv.data.api.ApiService
import com.example.tv.data.api.BannerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * UI state for a single rail including loading, optional error, and data list.
 */
data class RailState(
    val title: String,
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<ContentItem> = emptyList()
)

/**
 * PUBLIC_INTERFACE
 * ViewModel responsible for loading multiple category rails for the Home screen.
 * Triggers initial load of all categories. Exposes a StateFlow of rail states keyed by category.
 */
class HomeViewModel(
    private val repository: CategoryRepository = CategoryRepository(),
    private val api: ApiService = ApiService.create()
) : ViewModel() {

    private val categories = listOf(
        HomeCategory.TRENDING,
        HomeCategory.CONTINUE_WATCHING,
        HomeCategory.ACTION,
        HomeCategory.DRAMA,
        HomeCategory.HORROR,
        HomeCategory.FAMILY,
        HomeCategory.COMEDY
    )

    private val _state = MutableStateFlow<Map<HomeCategory, RailState>>(
        categories.associateWith { RailState(title = it.title, isLoading = true) }
    )
    val state: StateFlow<Map<HomeCategory, RailState>> = _state

    /** PUBLIC_INTERFACE
     * UI state for hero banner carousel.
     */
    data class BannerState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val banners: List<String> = emptyList()
    )

    private val _bannerState = MutableStateFlow(BannerState(isLoading = true))
    val bannerState: StateFlow<BannerState> = _bannerState

    /**
     * PUBLIC_INTERFACE
     * Start loading all categories in parallel.
     */
    fun loadAll() {
        // Load banners and rails in parallel
        loadBanners()
        categories.forEach { category ->
            loadCategory(category, refresh = true)
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Reload a specific category.
     */
    fun reload(category: HomeCategory) = loadCategory(category, refresh = true)

    /** PUBLIC_INTERFACE
     * Load banners from the backend.
     * Gracefully handle failures by exposing an empty list and error message.
     */
    fun loadBanners() {
        viewModelScope.launch(Dispatchers.IO) {
            _bannerState.value = BannerState(isLoading = true, error = null, banners = emptyList())
            try {
                val response: BannerResponse = api.getBanners()
                val urls = response.banners.filter { it.isNotBlank() }
                _bannerState.value = BannerState(isLoading = false, error = null, banners = urls)
            } catch (t: Throwable) {
                _bannerState.value = BannerState(isLoading = false, error = t.message ?: "Failed to load banners", banners = emptyList())
            }
        }
    }

    private fun loadCategory(category: HomeCategory, refresh: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            // set loading state
            _state.value = _state.value.toMutableMap().apply {
                put(category, (_state.value[category] ?: RailState(category.title)).copy(isLoading = true, error = null))
            }

            val result = repository.fetchCategory(category)
            val newState = result.fold(
                onSuccess = { items ->
                    RailState(title = category.title, isLoading = false, error = null, items = items)
                },
                onFailure = { e ->
                    RailState(title = category.title, isLoading = false, error = e.message ?: "Failed to load", items = emptyList())
                }
            )

            _state.value = _state.value.toMutableMap().apply { put(category, newState) }
        }
    }
}
