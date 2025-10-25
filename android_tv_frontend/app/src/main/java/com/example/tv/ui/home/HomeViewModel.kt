package com.example.tv.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PUBLIC_INTERFACE
 * HomeViewModel
 * Provides simple state for rails to keep the app compiling without the data layer.
 * Replace with real repository-backed implementation as needed.
 */
class HomeViewModel : ViewModel() {

    data class RailItem(
        val name: String,
        val poster: String? = null
    )

    data class RailState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val items: List<RailItem> = emptyList()
    )

    /**
     * PUBLIC_INTERFACE
     * State container keyed by category name for simplicity.
     */
    private val _state: MutableStateFlow<Map<String, RailState>> = MutableStateFlow(emptyMap())

    /**
     * PUBLIC_INTERFACE
     * Exposes immutable state map for UI.
     */
    val state: StateFlow<Map<String, RailState>> = _state.asStateFlow()

    /**
     * PUBLIC_INTERFACE
     * loadAll
     * Populates placeholder rails for compilation and UI testing without backend.
     */
    fun loadAll() {
        val demoItems = List(15) { idx -> RailItem(name = "Item ${idx + 1}", poster = null) }
        _state.value = mapOf(
            "TRENDING" to RailState(isLoading = false, error = null, items = demoItems),
            "CONTINUE_WATCHING" to RailState(isLoading = false, error = null, items = demoItems),
            "ACTION" to RailState(isLoading = false, error = null, items = demoItems),
            "DRAMA" to RailState(isLoading = false, error = null, items = demoItems),
            "HORROR" to RailState(isLoading = false, error = null, items = demoItems),
            "FAMILY" to RailState(isLoading = false, error = null, items = demoItems),
            "COMEDY" to RailState(isLoading = false, error = null, items = demoItems),
        )
    }
}
