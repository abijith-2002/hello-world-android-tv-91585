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
    val state: StateFlow<Map<HomeCategory, RailState>> = _state

    /**
     * PUBLIC_INTERFACE
     * Start loading all categories in parallel.
     */
    fun loadAll() {
        categories.forEach { category ->
            loadCategory(category, refresh = true)
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Reload a specific category.
     */
    fun reload(category: HomeCategory) = loadCategory(category, refresh = true)

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
