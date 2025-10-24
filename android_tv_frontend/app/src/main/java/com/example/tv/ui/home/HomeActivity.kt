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

        // Inflate static rail containers first
        val railViews = categories.associateWith { category ->
            val railView = layoutInflater.inflate(R.layout.view_rail, container, false)
            val titleView = railView.findViewById<TextView>(R.id.railTitle)
            titleView.text = category.title
            container.addView(railView)
            railView
        }

        // Collect state and populate rows
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
                    categories.forEach { category ->
                        val railState = stateMap[category] ?: return@forEach
                        val railView = railViews[category] ?: return@forEach

                        val row = railView.findViewById<LinearLayout>(R.id.railRow)
                        val titleView = railView.findViewById<TextView>(R.id.railTitle)
                        val scrollView = railView.findViewById<HorizontalScrollView>(R.id.railScroll)

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

                            // Hint Android focus system to search up towards topMenu
                            card.nextFocusUpId = R.id.topMenu

                            // Intercept DPAD_UP to move focus to the top menu when in top rails.
                            // This applies to any rail; behavior is safe for all.
                            card.setOnKeyListener { _, keyCode, event ->
                                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                    // Try focusing the menu's default child; if not available, focus menu container
                                    if (::topMenuDefaultChild.isInitialized && topMenuDefaultChild.isFocusable) {
                                        topMenuDefaultChild.requestFocus()
                                    } else if (::topMenu.isInitialized) {
                                        topMenu.requestFocus()
                                    }
                                    true
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

                        // Also set nextFocusUp for the row container and scroll view for robustness
                        scrollView.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu
                    }
                }
            }
        }
    }
}
