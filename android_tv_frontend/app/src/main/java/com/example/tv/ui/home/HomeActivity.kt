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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import coil.dispose
import com.example.tv.MainActivity
import com.example.tv.BuildConfig
import com.example.tv.R
import com.example.tv.data.api.HomeCategory
import com.example.tv.ui.content.ContentInfoActivity
import com.example.tv.ui.login.LoginActivity
import kotlinx.coroutines.launch
import coil.Coil

/**
 * PUBLIC_INTERFACE
 * HomeActivity
 * The TV Home screen with a top menu (Home, Login, Setting, My Plan), a hero banner carousel,
 * and multiple horizontal rails of content loaded from the API.
 * D-pad focus is enabled across interactive elements.
 *
 * DPAD_UP behavior:
 * - From any content row r > 0, DPAD_UP moves focus to the corresponding item in row r-1.
 * - For the first content row (r == 0), DPAD_UP moves focus to the top menu default item.
 *
 * - Accepts no parameters.
 * - Returns no value; displays UI.
 */
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    // Keep a stable reference to a focusable element in the top menu for requestFocus()
    private lateinit var topMenu: LinearLayout
    private lateinit var topMenuDefaultChild: View

    // Debug flag for focus mapping logs
    private val focusDebug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupTopMenu()
        setupHeroCarousel()
        setupRails()

        // Optional: clear Coil memory cache in debug builds to better simulate first-run verification
        if (BuildConfig.DEBUG) {
            try {
                Coil.imageLoader(this).memoryCache?.clear()
            } catch (_: Throwable) {
                // ignore
            }
        }

        // Trigger loads
        viewModel.loadAll()

        // Observe combined loading state to toggle global spinner AND hide other UI
        val loadingOverlay: View = findViewById(R.id.loading_overlay)
        val spinner: View = findViewById(R.id.circular_progress_indicator)
        val homeContent: View = findViewById(R.id.homeContent)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
                    // If any rail is still loading, show only the loader
                    val anyLoading = stateMap.values.any { it.isLoading }
                    loadingOverlay.visibility = if (anyLoading) View.VISIBLE else View.GONE
                    spinner.visibility = if (anyLoading) View.VISIBLE else View.GONE
                    homeContent.visibility = if (anyLoading) View.INVISIBLE else View.VISIBLE
                }
            }
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Sets up the hero banner carousel between top menu and rails.
     * - Fetches banners via ViewModel.bannerState
     * - Renders 480dp x 270dp cards matching rail card style
     * - Uses Coil with CENTER_CROP and measured size
     * - DPAD left/right wraps around (cyclic)
     * - Focused card animates and is centered horizontally via smoothScroll
     */
    private fun setupHeroCarousel() {
        val heroScroll = findViewById<HorizontalScrollView>(R.id.heroScroll)
        val heroRow = findViewById<LinearLayout>(R.id.heroRow)

        // Early return if layout not present (safety)
        if (heroScroll == null || heroRow == null) return

        fun centerFocusedChild(child: View) {
            // Smoothly center the focused child in the HorizontalScrollView
            val scrollView = heroScroll
            val childCenter = child.left + child.width / 2
            val containerCenter = scrollView.width / 2
            val targetScrollX = (childCenter - containerCenter).coerceAtLeast(0)
            scrollView.smoothScrollTo(targetScrollX, 0)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bannerState.collect { state ->
                    // Clear previous views
                    heroRow.removeAllViews()

                    if (state.isLoading) {
                        // Optional: show placeholder later if desired
                    } else if (state.error != null) {
                        // Graceful failure: leave empty and optionally notify once
                        Toast.makeText(this@HomeActivity, "Failed to load banners: ${state.error}", Toast.LENGTH_SHORT).show()
                    }

                    val urls = state.banners
                    if (urls.isEmpty()) {
                        // Nothing to render
                        return@collect
                    }

                    // Build hero cards
                    urls.forEachIndexed { _, url ->
                        val card = layoutInflater.inflate(R.layout.view_hero_card, heroRow, false)
                        val img = card.findViewById<ImageView>(R.id.heroImage)

                        // Ensure clipping to rounded outline
                        card.clipToOutline = true
                        img.adjustViewBounds = false
                        img.scaleType = ImageView.ScaleType.CENTER_CROP
                        img.cropToPadding = false

                        // Image loader consistent with rails
                        fun startLoadWithMeasuredSize() {
                            val w = img.width
                            val h = img.height
                            if (w <= 0 || h <= 0) return
                            try {
                                ImageCacheUtils.cancelOngoingRequest(img)
                            } catch (_: Throwable) { }
                            val data = url.takeIf { it.isNotBlank() } ?: R.drawable.thumb_1
                            img.load(data) {
                                scale(Scale.FILL)
                                size(w, h)
                                crossfade(false)
                                allowHardware(true)
                                memoryCachePolicy(CachePolicy.ENABLED)
                                placeholder(R.drawable.thumb_1)
                                error(R.drawable.thumb_2)
                                transformations(listOf())
                            }
                        }
                        if (img.width == 0 || img.height == 0) {
                            img.viewTreeObserver.addOnPreDrawListener(object : android.view.ViewTreeObserver.OnPreDrawListener {
                                override fun onPreDraw(): Boolean {
                                    if (img.width > 0 && img.height > 0) {
                                        img.viewTreeObserver.removeOnPreDrawListener(this)
                                        startLoadWithMeasuredSize()
                                    }
                                    return true
                                }
                            })
                        } else {
                            startLoadWithMeasuredSize()
                        }

                        card.isFocusable = true
                        card.isFocusableInTouchMode = true
                        card.nextFocusUpId = R.id.topMenu
                        card.setOnFocusChangeListener { v, hasFocus ->
                            v.animate().scaleX(if (hasFocus) 1.06f else 1.0f)
                                .scaleY(if (hasFocus) 1.06f else 1.0f)
                                .setDuration(140)
                                .start()
                            v.elevation = if (hasFocus)
                                resources.getDimension(R.dimen.card_elevation_focused)
                            else
                                resources.getDimension(R.dimen.card_elevation)

                            if (hasFocus) {
                                centerFocusedChild(v)
                            }
                        }

                        // Cyclic DPAD navigation
                        card.setOnKeyListener { v, keyCode, event ->
                            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                            when (keyCode) {
                                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                    val parent = v.parent as? LinearLayout ?: return@setOnKeyListener false
                                    val currentIndex = parent.indexOfChild(v).coerceAtLeast(0)
                                    val lastIndex = (parent.childCount - 1).coerceAtLeast(0)
                                    val atStart = currentIndex <= 0
                                    val atEnd = currentIndex >= lastIndex

                                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && atStart) {
                                        // Wrap to last
                                        parent.getChildAt(lastIndex)?.requestFocus()
                                        return@setOnKeyListener true
                                    }
                                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && atEnd) {
                                        // Wrap to first
                                        parent.getChildAt(0)?.requestFocus()
                                        return@setOnKeyListener true
                                    }
                                    false
                                }
                                else -> false
                            }
                        }

                        heroRow.addView(card)
                    }

                    // Ensure top menu DPAD_DOWN targets the first hero card if present
                    if (heroRow.childCount > 0) {
                        val firstHero = heroRow.getChildAt(0)
                        val menuHome = findViewById<TextView>(R.id.menuHome)
                        val menuLogin = findViewById<TextView>(R.id.menuLogin)
                        val menuSetting = findViewById<TextView>(R.id.menuSetting)
                        val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)
                        arrayOf(menuHome, menuLogin, menuSetting, menuMyPlan).forEach { menuItem ->
                            menuItem?.nextFocusDownId = firstHero.id
                        }
                        // Also set container fallback
                        findViewById<LinearLayout>(R.id.topMenu)?.nextFocusDownId = firstHero.id
                    }
                }
            }
        }
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
            // Allow DPAD_DOWN from any menu item to return focus to the rails/hero
            it.nextFocusDownId = R.id.railsContainer
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

        // Helpers to compute row index and remap focus up
        fun rowIndexOf(category: HomeCategory): Int = categories.indexOf(category).coerceAtLeast(0)

        /**
         * PUBLIC_INTERFACE
         * handleDpadUpWithinRails
         * General DPAD_UP mapping across rails: for any row r > 0, focus the corresponding
         * item by index in the immediate row above (r-1). For the first row (r == 0), jump to top menu.
         *
         * @param currentCard View currently focused within its row
         * @param currentCategory Category of the row containing currentCard
         * @return true if focus was remapped; false otherwise
         */
        fun handleDpadUpWithinRails(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx <= 0) {
                // First/top row: jump to the top menu default item
                if (::topMenuDefaultChild.isInitialized) {
                    topMenuDefaultChild.requestFocus()
                    if (focusDebug) Log.d("FocusNav", "DPAD_UP at first row: moving focus to top menu default")
                    return true
                }
                if (focusDebug) Log.d("FocusNav", "DPAD_UP at first row: top menu default not initialized")
                return false
            }

            val targetRowCategory = categories[currentRowIdx - 1]
            val targetRow = railRows[targetRowCategory] ?: return false

            // Guard: need at least one child to map to
            if (targetRow.childCount == 0) return false

            // Find current card index within its row
            val parentRow = currentCard.parent as? LinearLayout ?: return false
            val currentIndex = parentRow.indexOfChild(currentCard).coerceAtLeast(0)

            // Clamp to available children in target row
            val targetIndex = currentIndex.coerceAtMost(targetRow.childCount - 1)
            val target = targetRow.getChildAt(targetIndex)
            target?.requestFocus()

            if (focusDebug) {
                Log.d(
                    "FocusNav",
                    "DPAD_UP remap: row${currentRowIdx}[$currentIndex] -> row${currentRowIdx - 1}[$targetIndex]"
                )
            }
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

                            // Ensure the card itself honors outline clipping and uses compatibility padding
                            card.clipToOutline = true

                            // Ensure ImageView fills card with no empty borders
                            img.adjustViewBounds = false
                            img.scaleType = ImageView.ScaleType.CENTER_CROP
                            img.cropToPadding = false

                            // PUBLIC_INTERFACE
                            // Image loading behavior:
                            // - Wait until the ImageView is laid out to avoid 0x0 target size.
                            // - Provide exact size(img.width, img.height) and use Scale.FILL to match CENTER_CROP.
                            // - Disable crossfade/transformations to prevent visual zoom shifts.
                            fun startLoadWithMeasuredSize() {
                                val w = img.width
                                val h = img.height
                                if (w <= 0 || h <= 0) return

                                // Cancel any previous pending request tied to this ImageView to prevent reuse artifacts
                                try {
                                    ImageCacheUtils.cancelOngoingRequest(img)
                                } catch (_: Throwable) {
                                    // ignore
                                }

                                val data = item.poster?.takeIf { it.isNotBlank() } ?: R.drawable.thumb_1
                                img.load(data) {
                                    // Fill to avoid empty borders; minimal crop as needed
                                    scale(Scale.FILL)
                                    // Use exact target size for decode
                                    size(w, h)
                                    // Disable crossfade and transformations to avoid initial zoom/shift
                                    crossfade(false)
                                    // Prefer hardware when available, Coil will fallback if needed
                                    allowHardware(true)
                                    memoryCachePolicy(CachePolicy.ENABLED)
                                    placeholder(R.drawable.thumb_1)
                                    error(R.drawable.thumb_2)
                                    // Ensure no transformations are applied (stays empty)
                                    transformations(listOf())
                                }
                            }

                            if (img.width == 0 || img.height == 0) {
                                // Defer until first pre-draw (one-time listener)
                                img.viewTreeObserver.addOnPreDrawListener(object : android.view.ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        if (img.width > 0 && img.height > 0) {
                                            img.viewTreeObserver.removeOnPreDrawListener(this)
                                            startLoadWithMeasuredSize()
                                        }
                                        return true
                                    }
                                })
                            } else {
                                startLoadWithMeasuredSize()
                            }

                            // Keep only the image visible: hide title and overlay scrim explicitly
                            titleTv.text = ""
                            titleTv.visibility = View.GONE
                            overlay.visibility = View.GONE

                            // D-pad focus behavior
                            card.isFocusable = true
                            card.isFocusableInTouchMode = true

                            // Hint upwards to menu via XML nextFocusUp, but actual behavior will be
                            // overridden by our key listener mapping for rows > 0.
                            card.nextFocusUpId = R.id.topMenu

                            // Intercept DPAD_UP across all rows
                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        val handled = handleDpadUpWithinRails(v, category)
                                        handled
                                    }
                                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        // Prevent moving past horizontal bounds in the current rail row.
                                        val parentRow = v.parent as? LinearLayout
                                        if (parentRow != null) {
                                            val index = parentRow.indexOfChild(v).coerceAtLeast(0)
                                            val lastIndex = (parentRow.childCount - 1).coerceAtLeast(0)
                                            val atStart = index <= 0
                                            val atEnd = index >= lastIndex
                                            if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT && atStart) ||
                                                (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && atEnd)
                                            ) {
                                                if (focusDebug) {
                                                    Log.d(
                                                        "FocusNav",
                                                        "DPAD_${if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) "LEFT" else "RIGHT"} at boundary (index=$index, last=$lastIndex) — consuming."
                                                    )
                                                }
                                                return@setOnKeyListener true // consume to do nothing at bounds
                                            }
                                        }
                                        false // let normal navigation proceed between intermediate items
                                    }
                                    else -> false
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

                        // Keep focusUp hints for container views
                        scrollView.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu

                        // If this is the first row, set it as the nextFocusDown for the entire topMenu
                        if (categories.indexOf(category) == 0) {
                            // Assign nextFocusDown of each menu item to the first card if present
                            val firstCard = if (row.childCount > 0) row.getChildAt(0) else null
                            if (firstCard != null) {
                                val menuHome = findViewById<TextView>(R.id.menuHome)
                                val menuLogin = findViewById<TextView>(R.id.menuLogin)
                                val menuSetting = findViewById<TextView>(R.id.menuSetting)
                                val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

                                arrayOf(menuHome, menuLogin, menuSetting, menuMyPlan).forEach { menuItem ->
                                    menuItem?.nextFocusDownId = firstCard.id
                                }

                                // Also set container-level fallback
                                topMenu.nextFocusDownId = firstCard.id
                            }
                        }
                    }

                    // Verification logs for multiple rows
                    val counts = categories.mapIndexed { idx, c -> "row$idx=${railRows[c]?.childCount ?: 0}" }
                    if (focusDebug) {
                        Log.d("FocusNavVerify", "Rows children counts: ${counts.joinToString(", ")}. DPAD_UP maps r->r-1 by index with clamping.")
                    }
                }
            }
        }
    }
}
