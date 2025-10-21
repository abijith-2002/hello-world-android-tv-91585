package com.example.tv.ui.contentinfo

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 * A native Android TV "Content Info" screen that mirrors the Figma layout structure:
 * - Header with channel number and small channel name.
 * - Program name/title, details row (original title, duration, genres, age pill),
 *   timestamp row with "MÁS TARDE" tag and hours, and optional icons.
 * - Description text and a button panel (Play, Add, More) sized and spaced per design.
 *
 * Intent extras:
 * - EXTRA_TITLE (String): The main program title to display.
 * - EXTRA_SUBTITLE (String, optional): The subtitle/original title.
 * - EXTRA_DESCRIPTION (String, optional): The program description.
 * - EXTRA_POSTER_RES_ID (Int, optional): Drawable resource to use as background/poster art.
 * - EXTRA_CHANNEL_NUMBER (String, optional): Channel number label (e.g., "242").
 * - EXTRA_CHANNEL_NAME (String, optional): Channel display name (e.g., "TNT").
 *
 * Back navigation:
 * - The back button in the UI and the hardware BACK key both finish this activity.
 */
class ContentInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: ContentInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        // Read extras into a simple ViewModel
        viewModel = ContentInfoViewModel.fromIntent(intent)

        // Bind views
        val bgImage = findViewById<ImageView>(R.id.contentBackground)
        val btnBack: ImageButton? = findViewById(R.id.btnBack)
        if (btnBack == null) {
            Log.w("ContentInfoActivity", "btnBack view not found in layout; skipping back button setup")
        }
        val tvChannelNumber = findViewById<TextView>(R.id.tvChannelNumber)
        val tvChannelName = findViewById<TextView>(R.id.tvChannelName)
        val tvProgramName = findViewById<TextView>(R.id.tvProgramName)
        val tvOriginalTitle = findViewById<TextView>(R.id.tvOriginalTitle)
        val tvDuration = findViewById<TextView>(R.id.tvDuration)
        val tvGenres = findViewById<TextView>(R.id.tvGenres)
        val tvAge = findViewById<TextView>(R.id.tvAge)
        val tvLaterTag = findViewById<TextView>(R.id.tvLaterTag)
        val tvHourStart = findViewById<TextView>(R.id.tvHourStart)
        val tvHourEnd = findViewById<TextView>(R.id.tvHourEnd)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)

        val btnPlay = findViewById<View>(R.id.btnPlay)
        val btnAdd = findViewById<View>(R.id.btnAdd)
        val btnMore = findViewById<View>(R.id.btnMore)

        // Apply data
        if (viewModel.posterResId != null) {
            bgImage.setImageResource(viewModel.posterResId!!)
        } else {
            // Default to designed background image; resolve dynamically to avoid build-time symbol issues
            val fallbackRes = resources.getIdentifier("bg_content", "drawable", packageName)
            if (fallbackRes != 0) {
                bgImage.setImageResource(fallbackRes)
            } else {
                bgImage.setImageResource(R.drawable.banner_tv)
            }
        }
        tvChannelNumber.text = viewModel.channelNumber ?: getString(R.string.content_channel_number_default)
        tvChannelName.text = viewModel.channelName ?: getString(R.string.content_channel_name_default)

        tvProgramName.text = viewModel.title
        tvOriginalTitle.text = viewModel.subtitle ?: viewModel.title
        tvDuration.text = getString(R.string.content_duration_default) // Placeholder; can be replaced from data source
        tvGenres.text = getString(R.string.content_genres_default)
        tvAge.text = getString(R.string.content_age_default)

        tvLaterTag.text = getString(R.string.content_later_tag)
        tvHourStart.text = getString(R.string.content_hour_start_default)
        tvHourEnd.text = getString(R.string.content_hour_end_default)

        tvDescription.text = viewModel.description ?: getString(R.string.content_description_fallback)

        // Focus and interactions
        val focusScaler = View.OnFocusChangeListener { v, hasFocus ->
            v.animate().scaleX(if (hasFocus) 1.06f else 1.0f)
                .scaleY(if (hasFocus) 1.06f else 1.0f)
                .setDuration(120)
                .start()
            v.elevation = if (hasFocus) resources.getDimension(R.dimen.card_elevation_focused) else resources.getDimension(R.dimen.card_elevation)
        }
        // Apply focusability safely in case any view is missing from layout variations
        listOfNotNull<View>(btnPlay, btnAdd, btnMore, btnBack).forEach { v ->
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            v.onFocusChangeListener = focusScaler
        }

        btnBack?.setOnClickListener { finish() }
        btnPlay?.setOnClickListener {
            // Placeholder action - in a real app, start playback or schedule
            it.announceForAccessibility(getString(com.example.tv.R.string.content_action_play))
        }
        btnAdd?.setOnClickListener {
            it.announceForAccessibility(getString(com.example.tv.R.string.content_action_add))
        }
        btnMore?.setOnClickListener {
            it.announceForAccessibility(getString(com.example.tv.R.string.content_action_more))
        }

        // Handle BACK key
        findViewById<View>(R.id.contentInfoRoot)?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                finish()
                true
            } else {
                false
            }
        }

        // Initial focus on Play
        btnPlay?.requestFocus()
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SUBTITLE = "extra_subtitle"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_POSTER_RES_ID = "extra_poster_res_id"
        const val EXTRA_CHANNEL_NUMBER = "extra_channel_number"
        const val EXTRA_CHANNEL_NAME = "extra_channel_name"
    }
}
