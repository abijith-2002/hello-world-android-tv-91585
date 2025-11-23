package com.example.tv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv.data.BannerRepository
import com.example.tv.data.CategoryRepository
import com.example.tv.data.api.ContentItem
import com.example.tv.data.api.HomeCategory
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
 * UI state for hero banner carousel.
 */
data class BannerState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val banners: List<String> = emptyList(),
    val focusedIndex: Int = 0
)

/**
 * PUBLIC_INTERFACE
 * ViewModel responsible for loading multiple category rails and hero banners for the Home screen.
 * Triggers initial load of all categories and banners.
 * Exposes a StateFlow of rail states keyed by category and a BannerState flow.
 */
class HomeViewModel(
    private val repository: CategoryRepository = CategoryRepository(),
    private val bannerRepository: BannerRepository = BannerRepository()
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

    private val _bannerState = MutableStateFlow(BannerState(isLoading = true))
    val bannerState: StateFlow<BannerState> = _bannerState

    /**
     * PUBLIC_INTERFACE
     * Start loading all categories and banners in parallel.
     */
    fun loadAll() {
        // Rails
        categories.forEach { category ->
            loadCategory(category, refresh = true)
        }
        // Banners
        loadBanners()
    }

    /**
     * PUBLIC_INTERFACE
     * Reload a specific category.
     */
    fun reload(category: HomeCategory) = loadCategory(category, refresh = true)

    // PUBLIC_INTERFACE
    /**
     * Reload banners from API.
     */
    fun reloadBanners() = loadBanners()

    // PUBLIC_INTERFACE
    /**
     * Persist the currently focused banner index so it can be restored across recompositions/rotations.
     */
    fun setBannerFocusedIndex(index: Int) {
        _bannerState.value = _bannerState.value.copy(focusedIndex = index.coerceAtLeast(0))
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

    private fun loadBanners() {
        viewModelScope.launch(Dispatchers.IO) {
            _bannerState.value = _bannerState.value.copy(isLoading = true, error = null)
            val result = bannerRepository.fetchBanners()
            val newState = result.fold(
                onSuccess = { list ->
                    BannerState(isLoading = false, error = null, banners = list, focusedIndex = _bannerState.value.focusedIndex.coerceAtMost((list.size - 1).coerceAtLeast(0)))
                },
                onFailure = { e ->
                    BannerState(isLoading = false, error = e.message ?: "Failed to load banners", banners = emptyList(), focusedIndex = 0)
                }
            )
            _bannerState.value = newState
        }
    }
}
