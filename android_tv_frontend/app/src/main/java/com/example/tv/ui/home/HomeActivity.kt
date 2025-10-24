package com.example.tv.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.util.Log
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.request.CachePolicy
import com.example.tv.MainActivity
import com.example.tv.R
import com.example.tv.data.api.HomeCategory
import com.example.tv.ui.content.ContentInfoActivity
import com.example.tv.ui.login.LoginActivity
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * HomeActivity
 * The TV Home screen with a top menu (Home, Login, Setting, My Plan), a banner,
 * and multiple horizontal rails of content loaded from the API.
 * D-pad focus is enabled across interactive elements.
 *
 * - Accepts no parameters.
 * - Returns no value; displays UI.
 */
class HomeActivity : AppCompatActivity() {
    /**
     * PUBLIC_INTERFACE
     * DPAD navigation note:
     * While focus is on any content card in the rails, pressing DPAD_UP programmatically moves
     * focus to the top menu (defaults to the "Home" menu item). Other directions remain unchanged.
     */

    private val viewModel: HomeViewModel by viewModels()

    // Keep a stable reference to a focusable element in the top menu for requestFocus()
    private lateinit var topMenu: LinearLayout
    private lateinit var topMenuDefaultChild: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupTopMenu()
        setupRails()

        // Trigger loads
        viewModel.loadAll()
    }

    /**
     * PUBLIC_INTERFACE
     * Ensures the top menu has a stable focus target and basic interactions.
     */
    private fun setupTopMenu() {
        topMenu = findViewById(R.id.topMenu)
        val menuHome = findViewById<TextView>(R.id.menuHome)
        val menuLogin = findViewById<TextView>(R.id.menuLogin)
        val menuSetting = findViewById<TextView>(R.id.menuSetting)
        val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

        // Default child to receive focus when navigating up from rails
        topMenuDefaultChild = menuHome

        val focusScaler = View.OnFocusChangeListener { v, hasFocus ->
            v.animate().scaleX(if (hasFocus) 1.06f else 1.0f)
                .scaleY(if (hasFocus) 1.06f else 1.0f)
                .setDuration(120)
                .start()
        }

        arrayOf(menuHome, menuLogin, menuSetting, menuMyPlan).forEach {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.onFocusChangeListener = focusScaler
        }

        menuLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        menuMyPlan.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        menuHome.requestFocus()
    }

    private fun setupRails() {
        val container = findViewById<LinearLayout>(R.id.railsContainer)
        val categories = listOf(
            HomeCategory.TRENDING,
            HomeCategory.CONTINUE_WATCHING,
            HomeCategory.ACTION,
            HomeCategory.DRAMA,
            HomeCategory.HORROR,
            HomeCategory.FAMILY,
            HomeCategory.COMEDY
        )

        // Track row containers to compute "row index" for focus behavior mapping
        val railViews = mutableMapOf<HomeCategory, View>()
        val railRows = mutableMapOf<HomeCategory, LinearLayout>()
        val railScrolls = mutableMapOf<HomeCategory, HorizontalScrollView>()

        // Inflate static rail containers first
        categories.forEach { category ->
            val railView = layoutInflater.inflate(R.layout.view_rail, container, false)
            val titleView = railView.findViewById<TextView>(R.id.railTitle)
            titleView.text = category.title
            container.addView(railView)
            railViews[category] = railView
            railRows[category] = railView.findViewById(R.id.railRow)
            railScrolls[category] = railView.findViewById(R.id.railScroll)
        }

        // Helper to find the row index among all rails
        fun rowIndexOf(category: HomeCategory): Int = categories.indexOf(category).coerceAtLeast(0)

        // PUBLIC_INTERFACE
        // Map focus upward from the "second row" to the corresponding child in the "first row".
        // This ensures pressing DPAD_UP on any item in the second rail focuses the item above it
        // rather than the top menu.
        fun handleSecondRowDpadUp(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx != 1) return false // only remap for second row (index 1)

            val firstRow = railRows[categories[0]] ?: return false
            if (firstRow.childCount == 0) return false

            // Find current card index within its row
            val parentRow = currentCard.parent as? LinearLayout ?: return false
            val currentIndex = parentRow.indexOfChild(currentCard).coerceAtLeast(0)

            // Clamp to available children in first row
            val targetIndex = currentIndex.coerceAtMost(firstRow.childCount - 1)
            val target = firstRow.getChildAt(targetIndex)
            target?.requestFocus()
            Log.d("FocusNav", "DPAD_UP remap: row2[$currentIndex] -> row1[$targetIndex]")
            return true
        }

        // Collect state and populate rows
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
                    categories.forEach { category ->
                        val railState = stateMap[category] ?: return@forEach
                        val railView = railViews[category] ?: return@forEach

                        val row = railRows[category] ?: return@forEach
                        val titleView = railView.findViewById<TextView>(R.id.railTitle)
                        val scrollView = railScrolls[category] ?: return@forEach

                        // Clear previous
                        row.removeAllViews()

                        when {
                            railState.isLoading -> {
                                titleView.text = "${category.title} • Loading…"
                            }
                            railState.error != null -> {
                                titleView.text = "${category.title} • Error"
                                Toast.makeText(this@HomeActivity, "Failed to load ${category.title}: ${railState.error}", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                titleView.text = category.title
                            }
                        }

                        // Populate items
                        railState.items.forEach { item ->
                            val card = layoutInflater.inflate(R.layout.view_thumb_card, row, false)
                            val img = card.findViewById<ImageView>(R.id.thumbImage)
                            val titleTv = card.findViewById<TextView>(R.id.thumbTitle)
                            val overlay = card.findViewById<View>(R.id.overlayGrad)

                            // Load image with Coil using placeholder/error
                            Log.d("CoilTest", "Loading image URL: ${item.poster}")

                            img.load(item.poster) {
                                crossfade(true)
                                memoryCachePolicy(CachePolicy.ENABLED)
                                placeholder(R.drawable.thumb_1)
                                error(R.drawable.thumb_2)
                            }

                            // Show title overlay
                            titleTv.text = item.name
                            titleTv.visibility = View.VISIBLE
                            overlay.visibility = View.VISIBLE

                            // D-pad focus behavior
                            card.isFocusable = true
                            card.isFocusableInTouchMode = true

                            // Default hint upwards to topMenu, but we will override in key handler for second row
                            card.nextFocusUpId = R.id.topMenu

                            // Intercept DPAD_UP:
                            // - If card belongs to second row (index 1), focus corresponding index in first row.
                            // - Else, keep existing behavior to move up to top menu.
                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                    val remapped = handleSecondRowDpadUp(v, category)
                                    if (remapped) {
                                        true
                                    } else {
                                        // Fallback to menu focusing for other rows as before
                                        if (::topMenuDefaultChild.isInitialized && topMenuDefaultChild.isFocusable) {
                                            topMenuDefaultChild.requestFocus()
                                        } else if (::topMenu.isInitialized) {
                                            topMenu.requestFocus()
                                        }
                                        true
                                    }
                                } else {
                                    false
                                }
                            }

                            card.setOnFocusChangeListener { v, hasFocus ->
                                v.animate().scaleX(if (hasFocus) 1.06f else 1.0f)
                                    .scaleY(if (hasFocus) 1.06f else 1.0f)
                                    .setDuration(120)
                                    .start()
                                v.elevation = if (hasFocus)
                                    resources.getDimension(R.dimen.card_elevation_focused)
                                else
                                    resources.getDimension(R.dimen.card_elevation)
                            }

                            // Open ContentInfoActivity on click with name
                            card.setOnClickListener {
                                val intent = ContentInfoActivity.createIntent(
                                    context = this@HomeActivity,
                                    programTitle = item.name,
                                    description = "Details for ${item.name}",
                                    genres = "TV Show"
                                )
                                startActivity(intent)
                            }

                            row.addView(card)
                        }

                        // For rows other than the second one, keep nextFocusUp = topMenu so behavior remains unchanged
                        scrollView.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu
                    }

                    // Verification logs: when both first and second rows exist and have children, log mapping info
                    val first = railRows[categories.getOrNull(0)]
                    val second = railRows[categories.getOrNull(1)]
                    if (first != null && second != null && first.childCount > 0 && second.childCount > 0) {
                        Log.d("FocusNavVerify", "First row children: ${first.childCount}, Second row children: ${second.childCount}. DPAD_UP from any second-row item will map by index to first-row.")
                    }
                }
            }
        }
    }
}
