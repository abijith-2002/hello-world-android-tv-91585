package com.example.tv.ui.home

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
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
 * The TV Home screen with a top menu (Home, Login, Setting, My Plan), a banner,
 * and multiple horizontal rails of content loaded from the API.
 * D-pad focus is enabled across interactive elements.
 *
 * Custom DPAD behavior implemented:
 * - DPAD_DOWN from any top nav item focuses the hero banner (container).
 * - DPAD_UP from any item in the first rails row focuses the hero banner (container).
 * - DPAD_UP from the hero banner focuses the 'Home' button (topNavHome).
 * - DPAD_DOWN from the hero banner focuses the first item of the first rails row.
 *
 * Additionally:
 * - Detects when focus enters the hero carousel due to DPAD_UP and scrolls the parent
 *   ScrollView to the very top so the top navigation bar is fully visible.
 */
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    // Keep a stable reference to a focusable element in the top menu for requestFocus()
    private lateinit var topMenu: LinearLayout
    private lateinit var topMenuDefaultChild: View

    // Parent vertical scroll container
    private lateinit var contentScroll: ScrollView

    // Hero banner widgets
    private lateinit var bannerScroll: HorizontalScrollView
    private lateinit var bannerRow: LinearLayout

    // Tracks the last navigation direction key (true when last ACTION_DOWN was DPAD_UP)
    private var lastNavWasUp: Boolean = false

    // Debug flag for focus mapping logs
    private val focusDebug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Scroll container for the whole screen
        contentScroll = findViewById(R.id.homeContent)

        setupTopMenu()
        setupRails()
        setupBannerCarousel()

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
     * Overrides key dispatch to capture DPAD_UP navigation intent.
     * This does not consume the event; it only records that the last navigation was an UP,
     * allowing focus listeners to adjust scroll position after focus changes.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            lastNavWasUp = (event.keyCode == KeyEvent.KEYCODE_DPAD_UP)
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * PUBLIC_INTERFACE
     * Ensures the top menu has a stable focus target and basic interactions.
     */
    private fun setupTopMenu() {
        topMenu = findViewById(R.id.topMenu)
        val menuHome = findViewById<TextView>(R.id.topNavHome)
        val menuLogin = findViewById<TextView>(R.id.menuLogin)
        val menuSetting = findViewById<TextView>(R.id.menuSetting)
        val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

        // Default child to receive focus when navigating up from hero banner
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

            // Custom: DPAD_DOWN from any top nav item -> hero banner
            it.nextFocusDownId = R.id.heroBanner
            it.setOnKeyListener { _, keyCode, event ->
                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    val moved = moveFocusToHeroBanner()
                    if (focusDebug) Log.d("FocusNav", "TopNav DOWN -> heroBanner (moved=$moved)")
                    moved
                } else {
                    false
                }
            }
        }

        // Also set container-level mapping just in case container itself gets focus
        topMenu.nextFocusDownId = R.id.heroBanner
        topMenu.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                val moved = moveFocusToHeroBanner()
                if (focusDebug) Log.d("FocusNav", "topMenu DOWN -> heroBanner (moved=$moved)")
                moved
            } else {
                false
            }
        }

        menuLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        menuMyPlan.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        menuHome.requestFocus()
    }

    /**
     * PUBLIC_INTERFACE
     * Setup the hero banner carousel: binds loading/error UI, inflates cards from URLs,
     * handles DPAD focus wrap and smooth centering, and integrates focus transitions with
     * top menu and rails.
     *
     * Also detects when focus enters the hero carousel due to DPAD_UP and scrolls the parent
     * ScrollView to the top to ensure the top navigation bar is fully visible.
     */
    private fun setupBannerCarousel() {
        bannerScroll = findViewById(R.id.heroBanner)
        bannerRow = findViewById(R.id.bannerRow)

        // Ensure UP from any hero banner descendant targets the Home button explicitly
        bannerScroll.nextFocusUpId = R.id.topNavHome
        bannerRow.nextFocusUpId = R.id.topNavHome
        if (focusDebug) {
            Log.d("FocusNavVerify", "nextFocusUp configured: heroBanner->topNavHome")
        }

        val stateRow: View = findViewById(R.id.bannerStateRow)
        val loading: View = findViewById(R.id.bannerLoading)
        val errorText: TextView = findViewById(R.id.bannerErrorText)
        val retry: TextView = findViewById(R.id.bannerRetry)

        // Ensure hero banner container is focusable and prioritizes children for focus resolution
        bannerScroll.isFocusable = true
        bannerScroll.isFocusableInTouchMode = true
        bannerScroll.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        // When hero banner container receives focus, forward it to first/selected child if present,
        // and ensure scroll-to-top if focus entered due to DPAD_UP from below.
        bannerScroll.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                ensureTopScrollIfNeeded(trigger = "hero-container-focus")
                if (bannerRow.childCount > 0) {
                    val preferredIndex = viewModel.bannerState.value.focusedIndex
                        .coerceAtMost((bannerRow.childCount - 1).coerceAtLeast(0))
                    val target = bannerRow.getChildAt(preferredIndex) ?: bannerRow.getChildAt(0)
                    target?.requestFocus()
                }
            }
        }

        // Intercept DPAD at the banner container level to support UP -> Home, DOWN -> First rail
        bannerScroll.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    // Maintain wrap-around behavior at container-level for edge focus
                    val focused = bannerRow.focusedChild ?: return@setOnKeyListener false
                    val idx = bannerRow.indexOfChild(focused).coerceAtLeast(0)
                    val last = (bannerRow.childCount - 1).coerceAtLeast(0)
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && idx <= 0) {
                        val target = bannerRow.getChildAt(last)
                        target?.requestFocus()
                        bannerScroll.post {
                            val cx = computeCenterScrollX(bannerScroll, target)
                            bannerScroll.smoothScrollTo(cx, 0)
                        }
                        return@setOnKeyListener true
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && idx >= last) {
                        val target = bannerRow.getChildAt(0)
                        target?.requestFocus()
                        bannerScroll.post {
                            val cx = computeCenterScrollX(bannerScroll, target)
                            bannerScroll.smoothScrollTo(cx, 0)
                        }
                        return@setOnKeyListener true
                    }
                    false
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    val moved = moveFocusToTopNavHome()
                    if (focusDebug) Log.d("FocusNav", "HeroBanner UP -> topNavHome (moved=$moved)")
                    moved
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    val moved = moveFocusToFirstRailFirstItem()
                    if (focusDebug) Log.d("FocusNav", "HeroBanner DOWN -> firstRailFirstItem (moved=$moved)")
                    moved
                }
                else -> false
            }
        }

        // Retry affordance
        retry.setOnClickListener { viewModel.reloadBanners() }

        // Observe banner state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bannerState.collect { bState ->
                    // Toggle state views
                    val showLoading = bState.isLoading
                    val showError = bState.error != null
                    stateRow.visibility = if (showLoading || showError) View.VISIBLE else View.GONE
                    loading.visibility = if (showLoading) View.VISIBLE else View.GONE
                    errorText.visibility = if (showError) View.VISIBLE else View.GONE
                    retry.visibility = if (showError) View.VISIBLE else View.GONE

                    errorText.text = bState.error ?: getString(R.string.banner_error_generic)

                    // Populate banner cards
                    bannerRow.removeAllViews()
                    val banners = bState.banners

                    if (banners.isNotEmpty()) {
                        banners.forEachIndexed { index, url ->
                            val card = layoutInflater.inflate(R.layout.view_banner_card, bannerRow, false)
                            // Ensure unique ID per card for precise focus targeting
                            card.id = View.generateViewId()
                            val img = card.findViewById<ImageView>(R.id.bannerImage)

                            // Load with Coil (simple call; CENTER_CROP set in XML to minimize cropping)
                            try {
                                ImageCacheUtils.cancelOngoingRequest(img)
                            } catch (_: Throwable) {}
                            img.load(url)

                            // Focus animations + center on focus; also ensure scroll-to-top if entering via DPAD_UP
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
                                    ensureTopScrollIfNeeded(trigger = "hero-card-focus")
                                    val centerTarget = computeCenterScrollX(bannerScroll, v)
                                    bannerScroll.smoothScrollTo(centerTarget, 0)
                                    viewModel.setBannerFocusedIndex(index)
                                }
                            }

                            // Ensure system focus search knows where UP should go (Home)
                            card.nextFocusUpId = R.id.topNavHome

                            // Intercepts LEFT/RIGHT wrap-around; UP -> Home; DOWN -> first rail first item
                            card.setOnKeyListener { _, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                val parent = card.parent as? LinearLayout ?: return@setOnKeyListener false
                                val idx = parent.indexOfChild(card).coerceAtLeast(0)
                                val last = (parent.childCount - 1).coerceAtLeast(0)
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                                        if (idx <= 0) {
                                            val target = parent.getChildAt(last)
                                            target?.requestFocus()
                                            bannerScroll.post {
                                                val cx = computeCenterScrollX(bannerScroll, target)
                                                bannerScroll.smoothScrollTo(cx, 0)
                                            }
                                            return@setOnKeyListener true
                                        }
                                        false
                                    }
                                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        if (idx >= last) {
                                            val target = parent.getChildAt(0)
                                            target?.requestFocus()
                                            bannerScroll.post {
                                                val cx = computeCenterScrollX(bannerScroll, target)
                                                bannerScroll.smoothScrollTo(cx, 0)
                                            }
                                            return@setOnKeyListener true
                                        }
                                        false
                                    }
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        val moved = moveFocusToTopNavHome()
                                        if (focusDebug) Log.d("FocusNav", "BannerCard UP -> topNavHome (moved=$moved)")
                                        moved
                                    }
                                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                                        val moved = moveFocusToFirstRailFirstItem()
                                        if (focusDebug) Log.d("FocusNav", "BannerCard DOWN -> firstRailFirstItem (moved=$moved)")
                                        moved
                                    }
                                    else -> false
                                }
                            }

                            bannerRow.addView(card)
                        }

                        // Restore focus to saved index if possible; else focus first
                        val toFocus = viewModel.bannerState.value.focusedIndex
                            .coerceAtMost((bannerRow.childCount - 1).coerceAtLeast(0))
                        val focusView = bannerRow.getChildAt(toFocus)

                        if (focusView != null) {
                            // Keep top nav DOWN directed to hero banner container per requirement
                            val menuHome = findViewById<TextView>(R.id.topNavHome)
                            val menuLogin = findViewById<TextView>(R.id.menuLogin)
                            val menuSetting = findViewById<TextView>(R.id.menuSetting)
                            val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

                            menuHome?.nextFocusDownId = R.id.heroBanner
                            menuLogin?.nextFocusDownId = R.id.heroBanner
                            menuSetting?.nextFocusDownId = R.id.heroBanner
                            menuMyPlan?.nextFocusDownId = R.id.heroBanner
                            topMenu.nextFocusDownId = R.id.heroBanner

                            // Center the initially focused banner without animation
                            bannerScroll.post {
                                val cx = computeCenterScrollX(bannerScroll, focusView)
                                bannerScroll.scrollTo(cx, 0)
                            }
                        }
                    } else {
                        // No banners; nothing special
                    }
                }
            }
        }
    }

    /**
     * Compute scroll X to center the child view inside the given HorizontalScrollView.
     */
    private fun computeCenterScrollX(scroll: HorizontalScrollView, child: View?): Int {
        if (child == null) return 0
        val scrollWidth = scroll.width
        val childCenter = (child.left + child.right) / 2
        return childCenter - scrollWidth / 2
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

        fun rowIndexOf(category: HomeCategory): Int = categories.indexOf(category).coerceAtLeast(0)

        /**
         * PUBLIC_INTERFACE
         * handleDpadUpWithinRails
         * General DPAD_UP mapping across rails:
         * - For any row r > 0, focus the corresponding item by index in the immediate row above (r-1).
         * - For the first content row (r == 0), DPAD_UP moves focus to the hero banner container.
         *
         * @param currentCard View currently focused within its row
         * @param currentCategory Category of the row containing currentCard
         * @return true if focus was remapped; false if boundary/no-op
         */
        fun handleDpadUpWithinRails(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx <= 0) {
                // Requirement: From first rail UP -> hero banner
                val moved = moveFocusToHeroBanner()
                if (focusDebug) Log.d("FocusNav", "DPAD_UP from first rail -> heroBanner (moved=$moved)")
                return moved
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

                            // Ensure images are clipped to rounded corners (card_bg sets rounded outline)
                            (card.parent as? View)?.let { container ->
                                container.clipToOutline = true
                            }
                            // Also ensure the card itself honors outline clipping and uses compatibility padding
                            card.clipToOutline = true

                            // Ensure ImageView fills card with no empty borders
                            img.adjustViewBounds = false
                            img.scaleType = ImageView.ScaleType.CENTER_CROP
                            img.cropToPadding = false

                            // Load image with Coil using placeholder/error
                            Log.d("CoilFirstLoad", "Loading image URL: ${item.poster}")

                            // Image loading behavior:
                            fun startLoadWithMeasuredSize() {
                                val w = img.width
                                val h = img.height
                                if (w <= 0 || h <= 0) return

                                try {
                                    ImageCacheUtils.cancelOngoingRequest(img)
                                } catch (_: Throwable) {
                                    // ignore
                                }

                                val data = item.poster?.takeIf { it.isNotBlank() } ?: R.drawable.thumb_1
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

                            // Keep only the image visible: hide title and overlay scrim explicitly
                            titleTv.text = ""
                            titleTv.visibility = View.GONE
                            overlay.visibility = View.GONE

                            // D-pad focus behavior
                            card.isFocusable = true
                            card.isFocusableInTouchMode = true

                            // Hint upwards to Home for general fallback (listeners will override)
                            card.nextFocusUpId = R.id.topNavHome

                            // Intercept DPAD_UP across all rows:
                            // - If row index > 0, focus the corresponding index in row-1.
                            // - If row index == 0, move focus to hero banner container.
                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        val handled = handleDpadUpWithinRails(v, category)
                                        handled
                                    }
                                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                                        // If this is the first row, DOWN remains default (to row 2).
                                        // No special handling for rows > 0.
                                        false
                                    }
                                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        // Maintain boundary consumption to avoid leaking focus vertically/horizontally
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
                                                return@setOnKeyListener true
                                            }
                                        }
                                        false
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

                            // Open ContentInfoActivity on click with name and pass poster URL for background
                            card.setOnClickListener {
                                val intent = ContentInfoActivity.createIntent(
                                    context = this@HomeActivity,
                                    programTitle = item.name,
                                    description = "Details for ${item.name}",
                                    genres = "TV Show",
                                    posterUrl = item.poster // may be null/blank; handled gracefully in ContentInfoActivity
                                )
                                startActivity(intent)
                            }

                            row.addView(card)
                        }

                        val rowIndex = categories.indexOf(category)

                        // Focus up: for first row, UP should go to hero banner container
                        if (::bannerScroll.isInitialized) {
                            if (rowIndex == 0) {
                                scrollView.nextFocusUpId = R.id.heroBanner
                                row.nextFocusUpId = R.id.heroBanner
                            } else {
                                // For other rows, fallback to top menu if search fails (listeners handle exact mapping)
                                scrollView.nextFocusUpId = R.id.topMenu
                                row.nextFocusUpId = R.id.topMenu
                            }
                        }

                        // If this is the first row, assign IDs for references and connect hero banner DOWN to its first item
                        if (rowIndex == 0) {
                            // Assign stable IDs to first rail row and its first item if present
                            row.id = R.id.firstRailRow
                            val firstCard = if (row.childCount > 0) row.getChildAt(0) else null
                            if (firstCard != null) {
                                firstCard.id = R.id.firstRailFirstItem
                                // From hero banner DOWN -> first rail first item
                                if (::bannerScroll.isInitialized) {
                                    bannerScroll.nextFocusDownId = firstCard.id
                                }
                            }
                        }
                    }

                    // Verification logs for multiple rows
                    val counts = categories.mapIndexed { idx, c -> "row$idx=${railRows[c]?.childCount ?: 0}" }
                    if (focusDebug) {
                        Log.d("FocusNavVerify", "Rows children counts: ${counts.joinToString(", ")}. Custom DPAD rules active for top/hero/first-row.")
                    }
                }
            }
        }
    }

    // Utils

    private fun moveFocusToHeroBanner(): Boolean {
        // Prefer focusing a child card if available
        val row = if (::bannerRow.isInitialized) bannerRow else null
        if (row != null && row.childCount > 0) {
            val preferredIndex = viewModel.bannerState.value.focusedIndex
                .coerceAtMost((row.childCount - 1).coerceAtLeast(0))
            val target = row.getChildAt(preferredIndex) ?: row.getChildAt(0)
            return target?.requestFocus() == true
        }
        val hero = findViewById<View>(R.id.heroBanner) ?: return false
        return hero.requestFocus()
    }

    private fun moveFocusToTopNavHome(): Boolean {
        val home = findViewById<View>(R.id.topNavHome)
        return if (home != null) {
            home.requestFocus()
        } else {
            if (::topMenuDefaultChild.isInitialized) {
                topMenuDefaultChild.requestFocus()
            } else {
                false
            }
        }
    }

    private fun moveFocusToFirstRailFirstItem(): Boolean {
        val row = findViewById<LinearLayout>(R.id.firstRailRow) ?: return false
        if (row.childCount <= 0) return false
        val first = row.getChildAt(0) ?: return false
        return first.requestFocus()
    }

    /**
     * When focus enters the hero carousel due to DPAD_UP from a lower section,
     * scroll the parent container to top so the top navigation bar is fully visible.
     * This runs immediately via scrollTo(0,0) to avoid visual jitter.
     */
    private fun ensureTopScrollIfNeeded(trigger: String = "unknown") {
        if (!::contentScroll.isInitialized) return
        if (!lastNavWasUp) return

        if (contentScroll.scrollY > 0) {
            // Immediate scroll to the very top to avoid jitter and ensure full visibility
            contentScroll.scrollTo(0, 0)

            // Verify nav bar visibility; if any clipping, enforce top again on next frame
            topMenu.post {
                val fullyVisible = isViewFullyVisible(topMenu)
                if (!fullyVisible) {
                    contentScroll.scrollTo(0, 0)
                }
            }
            if (focusDebug) Log.d("FocusNav", "Scroll-to-top executed on '$trigger'")
        }

        // Reset the flag so subsequent focus changes (e.g., LEFT/RIGHT in hero) do not retrigger
        lastNavWasUp = false
    }

    private fun isViewFullyVisible(v: View): Boolean {
        val r = Rect()
        val visible = v.getGlobalVisibleRect(r)
        return visible && r.height() >= v.height && r.width() >= v.width
    }

    private fun isDescendantOf(view: View?, parentId: Int): Boolean {
        if (view == null) return false
        var current: View? = view
        while (current != null) {
            if (current.id == parentId) return true
            val p = current.parent
            current = if (p is View) p else null
        }
        return false
    }
}
