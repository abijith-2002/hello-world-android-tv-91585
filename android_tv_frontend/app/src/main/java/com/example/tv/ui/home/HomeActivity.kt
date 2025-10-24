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
import coil.size.Size
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
 * DPAD_UP behavior:
 * - From any content row r > 0, DPAD_UP moves focus to the corresponding item in row r-1.
 * - For the first content row (r == 0), DPAD_UP does not jump to the top menu unless explicitly handled elsewhere.
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

        // Helpers to compute row index and remap focus up
        fun rowIndexOf(category: HomeCategory): Int = categories.indexOf(category).coerceAtLeast(0)

        /**
         * PUBLIC_INTERFACE
         * handleDpadUpWithinRails
         * General DPAD_UP mapping across rails: for any row r > 0, focus the corresponding
         * item by index in the immediate row above (r-1). For the first row (r == 0), keep
         * default behavior (no remap to top menu unless explicitly intended elsewhere).
         *
         * @param currentCard View currently focused within its row
         * @param currentCategory Category of the row containing currentCard
         * @return true if focus was remapped to the row above; false if boundary/no-op
         */
        fun handleDpadUpWithinRails(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx <= 0) {
                if (focusDebug) Log.d("FocusNav", "DPAD_UP at first row: boundary; not jumping to top menu.")
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

                            // Ensure images are clipped to rounded corners (card_bg sets rounded outline)
                            (card.parent as? View)?.let { container ->
                                // For the inner FrameLayout we set outline to background; enable clip
                                container.clipToOutline = true
                            }
                            // Also ensure the card itself honors outline clipping and uses compatibility padding
                            card.clipToOutline = true

                            // Ensure safe ImageView attributes to avoid implicit crops due to theme/defaults
                            img.adjustViewBounds = true
                            img.scaleType = ImageView.ScaleType.FIT_CENTER
                            img.cropToPadding = false

                            // Load image with Coil using placeholder/error
                            Log.d("CoilFirstLoad", "Loading image URL: ${item.poster}")

                            // PUBLIC_INTERFACE
                            // Image loading behavior:
                            // - Wait until the ImageView is laid out to avoid 0x0 target size.
                            // - Provide an explicit SizeResolver tied to the ImageView.
                            // - Use Scale.FIT and disable crossfade to prevent visual size jumps/crops.
                            // - No transformations are applied on first load.
                            fun startLoadWithMeasuredSize() {
                                // Compute exact measured bounds; guard against zero
                                val w = img.width
                                val h = img.height
                                if (w <= 0 || h <= 0) return

                                // Cancel any previous pending request tied to this ImageView (safety in dynamic UIs)
                                try {
                                    ImageCacheUtils.cancelOngoingRequest(img)
                                } catch (_: Throwable) {
                                    // best-effort; ignore if not supported
                                }

                                img.load(item.poster) {
                                    // Critical flags for first-load correctness
                                    crossfade(false)
                                    // Avoid deferring due to hardware bitmap + unknown size paths
                                    allowHardware(false)
                                    scale(coil.size.Scale.FIT)
                                    // Provide exact view-bound size in pixels
                                    size(w, h)
                                    // Keep caches enabled but avoid transformations
                                    memoryCachePolicy(CachePolicy.ENABLED)
                                    // No transformations on first load
                                    placeholder(R.drawable.thumb_1)
                                    error(R.drawable.thumb_2)
                                }
                            }

                            if (img.width == 0 || img.height == 0) {
                                // Defer until pre-draw/layout completes
                                img.viewTreeObserver.addOnPreDrawListener(object : android.view.ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        if (img.width > 0 && img.height > 0) {
                                            img.viewTreeObserver.removeOnPreDrawListener(this)
                                            startLoadWithMeasuredSize()
                                        }
                                        // Return true to continue drawing
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

                            // Intercept DPAD_UP across all rows:
                            // - If row index > 0, focus the corresponding index in row-1.
                            // - If row index == 0, do nothing special here (do not jump to menu).
                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                    val remapped = handleDpadUpWithinRails(v, category)
                                    if (remapped) {
                                        true
                                    } else {
                                        // First row boundary: do not auto-jump to menu. Let default system focus rules apply.
                                        if (focusDebug) Log.d("FocusNav", "First row DPAD_UP: staying within row/top boundary.")
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

                        // Keep focusUp hints for container views (no change needed)
                        scrollView.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu
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
