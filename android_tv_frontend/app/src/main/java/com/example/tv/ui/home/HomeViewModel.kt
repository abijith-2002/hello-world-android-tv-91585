package com.example.tv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * - isLoading controls the inline loader chip visibility and whether skeleton placeholders are shown.
 * - error is reflected in the rail header (localized “Error” label) without blocking other sections.
 * - items are rendered as soon as available; other rails can continue loading independently.
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
 *
 * Per-section loader behavior: Each category rail uses RailState.isLoading to show a tiny inline
 * loader chip and skeleton placeholders until that rail's data arrives. No global/full-screen
 * overlay loader is used; the screen layout is visible immediately on start.
 */
class HomeViewModel(
    private val repository: CategoryRepository = CategoryRepository()
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
    // PUBLIC_INTERFACE
    // Per-section rail states keyed by HomeCategory. Each RailState contains:
    // - isLoading: Boolean for inline loader visibility
    // - error: String? to indicate loading error
    // - items: List<ContentItem> loaded so far
    val state: StateFlow<Map<HomeCategory, RailState>> = _state

    // PUBLIC_INTERFACE
    /** Convenience flags for per-section loading states. */
    val isLoadingTrending: Boolean get() = _state.value[HomeCategory.TRENDING]?.isLoading == true
    val isLoadingContinueWatching: Boolean get() = _state.value[HomeCategory.CONTINUE_WATCHING]?.isLoading == true
    val isLoadingAction: Boolean get() = _state.value[HomeCategory.ACTION]?.isLoading == true
    val isLoadingDrama: Boolean get() = _state.value[HomeCategory.DRAMA]?.isLoading == true
    val isLoadingHorror: Boolean get() = _state.value[HomeCategory.HORROR]?.isLoading == true
    val isLoadingFamily: Boolean get() = _state.value[HomeCategory.FAMILY]?.isLoading == true
    val isLoadingComedy: Boolean get() = _state.value[HomeCategory.COMEDY]?.isLoading == true

    /**
     * PUBLIC_INTERFACE
     * Start loading all categories in parallel.
     */
    fun loadAll() {
        categories.forEach { category ->
            loadCategory(category)
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Reload a specific category.
     */
    fun reload(category: HomeCategory) = loadCategory(category)

    private fun loadCategory(category: HomeCategory) {
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
