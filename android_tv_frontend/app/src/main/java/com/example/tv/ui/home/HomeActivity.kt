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
import com.example.tv.ui.home.hide
import com.example.tv.ui.home.show
import com.example.tv.ui.home.DimenParseUtils

/**
 * PUBLIC_INTERFACE
 * HomeActivity
 * The TV Home screen with a top menu (Home, Login, Setting, My Plan), a banner,
 * and multiple horizontal rails of content loaded from the API.
 * D-pad focus is enabled across interactive elements.
 *
 * DPAD_UP behavior:
 * - From any content row r > 0, DPAD_UP moves focus to the corresponding item in row r-1.
 * - For the first content row (r == 0), DPAD_UP jumps to the default top menu item.
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
    private val focusDebug = BuildConfig.DEBUG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // No global overlay loader is present in activity_home.xml; ensure we never reference such IDs.

        setupTopMenu()
        setupRails()

        // Optional: clear Coil memory cache in debug builds to better simulate first-run verification
        if (BuildConfig.DEBUG) {
            try {
                Coil.imageLoader(this).memoryCache?.clear()
            } catch (_: Throwable) {
                // ignore
            }
        }

        // Trigger loads for all sections; UI remains visible and renders progressively
        // No full-screen/global overlay loader is used. Each section renders its own inline loader.
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
            try {
                val railView = layoutInflater.inflate(R.layout.view_rail, container, false)
                val titleView = railView.findViewById<TextView>(R.id.railTitle)
                titleView.text = category.title
                container.addView(railView)
                railViews[category] = railView
                railRows[category] = railView.findViewById(R.id.railRow)
                railScrolls[category] = railView.findViewById(R.id.railScroll)
            } catch (t: Throwable) {
                Log.e("HomeActivity", "Failed to inflate rail for ${category.title}: ${t.message}", t)
            }
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
         * @return true if focus was remapped to the row above; false if boundary/no-op
         */
        fun handleDpadUpWithinRails(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx <= 0) {
                // First row: route to top menu default item
                if (focusDebug) Log.d("FocusNav", "DPAD_UP at first row: moving focus to top menu default.")
                try {
                    topMenuDefaultChild.requestFocus()
                } catch (_: Throwable) {
                    try {
                        topMenu.requestFocus()
                    } catch (_: Throwable) {
                        // ignore
                    }
                }
                return true
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
        // Note: Do NOT introduce a global/full-screen loader here. Each rail has its own loading chip and skeletons.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
                    categories.forEach { category ->
                        val railState = stateMap[category] ?: return@forEach
                        val railView = railViews[category] ?: run {
                            Log.w("HomeActivity", "Rail view missing for ${category.title}, skipping update.")
                            return@forEach
                        }

                        val row = railRows[category] ?: return@forEach
                        val titleView = railView.findViewById<TextView>(R.id.railTitle) ?: return@forEach
                        val loadingChip = railView.findViewById<View>(R.id.railLoadingChip)
                        val emptyState = railView.findViewById<View>(R.id.railEmptyState)
                        val scrollView = railScrolls[category]

                        if (loadingChip == null) {
                            Log.w("HomeActivity", "railLoadingChip not found for ${category.title}")
                        }
                        if (emptyState == null) {
                            Log.w("HomeActivity", "railEmptyState not found for ${category.title}")
                        }

                        // Clear previous content
                        row.removeAllViews()

                        // Title and inline chip state
                        val safeTitle = category.title.ifBlank { getString(R.string.app_name) }
                        if (railState.isLoading) {
                            titleView.text = safeTitle
                            loadingChip?.show()
                            emptyState?.hide()
                        } else {
                            loadingChip?.hide()
                            if (railState.error != null) {
                                titleView.text = "$safeTitle • ${getString(R.string.label_error)}"
                                Toast.makeText(
                                    this@HomeActivity,
                                    getString(R.string.label_error) + ": " + railState.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                                emptyState?.hide()
                            } else {
                                titleView.text = safeTitle
                                // Show empty state only when loaded successfully with no items
                                if (railState.items.isEmpty()) {
                                    emptyState?.show()
                                } else {
                                    emptyState?.hide()
                                }
                            }
                        }

                        // If loading and no items yet, show skeleton placeholders using bg_skeleton_placeholder
                        if (railState.isLoading && railState.items.isEmpty()) {
                            repeat(6) {
                                val skeleton = layoutInflater.inflate(R.layout.view_thumb_card, row, false)
                                val img = skeleton.findViewById<ImageView>(R.id.thumbImage)
                                val titleTv = skeleton.findViewById<TextView>(R.id.thumbTitle)
                                val overlay = skeleton.findViewById<View>(R.id.overlayGrad)

                                // Nord dark skeleton placeholder
                                img.setImageResource(R.drawable.bg_skeleton_placeholder)
                                img.alpha = 0.8f
                                titleTv.visibility = View.GONE
                                overlay.visibility = View.GONE

                                skeleton.isFocusable = false
                                row.addView(skeleton)
                            }
                        }

                        // Populate real items as they arrive
                        railState.items.forEach { item ->
                            val card = layoutInflater.inflate(R.layout.view_thumb_card, row, false)
                            val img = card.findViewById<ImageView>(R.id.thumbImage)
                            val titleTv = card.findViewById<TextView>(R.id.thumbTitle)
                            val overlay = card.findViewById<View>(R.id.overlayGrad)

                            // Ensure images are clipped to rounded corners
                            (card.parent as? View)?.let { container ->
                                container.clipToOutline = true
                            }
                            card.clipToOutline = true

                            // Ensure ImageView fills card with no empty borders
                            img.adjustViewBounds = false
                            img.scaleType = ImageView.ScaleType.CENTER_CROP
                            img.cropToPadding = false

                            // Load image with Coil using placeholder/error
                            Log.d("CoilFirstLoad", "Loading image URL: ${item.poster}")

                            // PUBLIC_INTERFACE
                            // Image loading behavior with measured size and no crossfade/transformations
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

                            // Keep only the image visible
                            titleTv?.text = ""
                            titleTv?.visibility = View.GONE
                            overlay?.visibility = View.GONE

                            // D-pad focus behavior
                            card.isFocusable = true
                            card.isFocusableInTouchMode = true

                            // Hint upwards to menu via XML nextFocusUp, but mapping handled in key listener
                            card.nextFocusUpId = R.id.topMenu

                            // Intercept key events for row navigation behavior
                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        val remapped = handleDpadUpWithinRails(v, category)
                                        if (remapped) {
                                            true
                                        } else {
                                            if (focusDebug) Log.d("FocusNav", "First row DPAD_UP: staying within row/top boundary.")
                                            true
                                        }
                                    }
                                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
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
                        scrollView?.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu
                    }

                    // Verification logs for multiple rows: helps validate progressive loading across rails
                    val counts = categories.mapIndexed { idx, c -> "row$idx=${railRows[c]?.childCount ?: 0}" }
                    if (focusDebug) {
                        Log.d("FocusNavVerify", "Rows children counts: ${counts.joinToString(", ")}. DPAD_UP maps r->r-1 by index with clamping.")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Best-effort cleanup: clear Coil memory cache in debug and allow GC to reclaim bitmaps.
        if (BuildConfig.DEBUG) {
            try {
                Coil.imageLoader(this).memoryCache?.clear()
            } catch (_: Throwable) {
                // ignore
            }
        }
    }
}
