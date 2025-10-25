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
import coil.Coil
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import com.example.tv.BuildConfig
import com.example.tv.MainActivity
import com.example.tv.R
import com.example.tv.data.api.HomeCategory
import com.example.tv.ui.content.ContentInfoActivity
import com.example.tv.ui.login.LoginActivity
import com.example.tv.ui.home.ImageCacheUtils
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * HomeActivity
 * Displays top menu and content rails for the Android TV app.
 *
 * DPAD_UP behavior:
 * - From any content row r > 0: DPAD_UP moves focus to the corresponding index in row (r-1).
 * - From the first content row (r == 0): DPAD_UP requests focus on the top menu's first button (menuHome).
 */
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    // Top menu references
    private lateinit var topMenu: LinearLayout
    private lateinit var topMenuDefaultChild: View

    private val focusDebug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupTopMenu()
        setupRails()

        if (BuildConfig.DEBUG) {
            try {
                Coil.imageLoader(this).memoryCache?.clear()
            } catch (_: Throwable) {
                // ignore
            }
        }

        // Trigger loads
        viewModel.loadAll()

        // Global loader and content toggle
        val loadingOverlay: View = findViewById(R.id.loading_overlay)
        val spinner: View = findViewById(R.id.circular_progress_indicator)
        val homeContent: View = findViewById(R.id.homeContent)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
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
     * Prepare the top menu and default focus target.
     */
    private fun setupTopMenu() {
        topMenu = findViewById(R.id.topMenu)
        val menuHome = findViewById<TextView>(R.id.menuHome)
        val menuLogin = findViewById<TextView>(R.id.menuLogin)
        val menuSetting = findViewById<TextView>(R.id.menuSetting)
        val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

        // Default child for first-row DPAD_UP
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

        val railViews = mutableMapOf<HomeCategory, View>()
        val railRows = mutableMapOf<HomeCategory, LinearLayout>()
        val railScrolls = mutableMapOf<HomeCategory, HorizontalScrollView>()

        // Inflate rail sections
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
         * Handle DPAD_UP mapping: row r -> r-1 by index; for r == 0, move focus to top menu.
         */
        fun handleDpadUpWithinRails(currentCard: View, currentCategory: HomeCategory): Boolean {
            val currentRowIdx = rowIndexOf(currentCategory)
            if (currentRowIdx <= 0) {
                if (focusDebug) Log.d("FocusNav", "DPAD_UP at first row: moving focus to top menu.")
                return if (::topMenuDefaultChild.isInitialized && topMenuDefaultChild.isFocusable) {
                    topMenuDefaultChild.requestFocus()
                } else {
                    topMenu.requestFocus()
                }
            }

            val targetRowCategory = categories[currentRowIdx - 1]
            val targetRow = railRows[targetRowCategory] ?: return false
            if (targetRow.childCount == 0) return false

            val parentRow = currentCard.parent as? LinearLayout ?: return false
            val currentIndex = parentRow.indexOfChild(currentCard).coerceAtLeast(0)
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

        // Populate items from state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { stateMap ->
                    categories.forEach { category ->
                        val railState = stateMap[category] ?: return@forEach
                        val railView = railViews[category] ?: return@forEach

                        val row = railRows[category] ?: return@forEach
                        val titleView = railView.findViewById<TextView>(R.id.railTitle)
                        val scrollView = railScrolls[category] ?: return@forEach

                        row.removeAllViews()

                        when {
                            railState.isLoading -> {
                                titleView.text = "${category.title} • Loading…"
                            }
                            railState.error != null -> {
                                titleView.text = "${category.title} • Error"
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Failed to load ${category.title}: ${railState.error}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> titleView.text = category.title
                        }

                        railState.items.forEach { item ->
                            val card = layoutInflater.inflate(R.layout.view_thumb_card, row, false)
                            val img = card.findViewById<ImageView>(R.id.thumbImage)
                            val titleTv = card.findViewById<TextView>(R.id.thumbTitle)
                            val overlay = card.findViewById<View>(R.id.overlayGrad)

                            // Clip to rounded corners (defined by card_bg)
                            card.clipToOutline = true

                            img.adjustViewBounds = false
                            img.scaleType = ImageView.ScaleType.CENTER_CROP
                            img.cropToPadding = false

                            // Load image with Coil
                            fun startLoadWithMeasuredSize() {
                                val w = img.width
                                val h = img.height
                                if (w <= 0 || h <= 0) return

                                try {
                                    ImageCacheUtils.cancelOngoingRequest(img)
                                } catch (_: Throwable) {
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
                                img.viewTreeObserver.addOnPreDrawListener(object :
                                    android.view.ViewTreeObserver.OnPreDrawListener {
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

                            // Hide text and overlay by default (image-only)
                            titleTv.text = ""
                            titleTv.visibility = View.GONE
                            overlay.visibility = View.GONE

                            // Focus behavior
                            card.isFocusable = true
                            card.isFocusableInTouchMode = true
                            card.nextFocusUpId = R.id.topMenu

                            card.setOnKeyListener { v, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        val handled = handleDpadUpWithinRails(v, category)
                                        if (handled) true
                                        else {
                                            if (focusDebug) Log.d("FocusNav", "DPAD_UP not handled; consuming.")
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
                                                        "DPAD_${if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) "LEFT" else "RIGHT"} boundary index=$index last=$lastIndex; consuming."
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

                        scrollView.nextFocusUpId = R.id.topMenu
                        row.nextFocusUpId = R.id.topMenu
                    }

                    val counts = categories.mapIndexed { idx, c ->
                        "row$idx=${railRows[c]?.childCount ?: 0}"
                    }
                    if (focusDebug) {
                        Log.d(
                            "FocusNavVerify",
                            "Rows children counts: ${counts.joinToString(", ")}; first row DPAD_UP -> menu."
                        )
                    }
                }
            }
        }
    }
}
