package com.example.tv.ui.content

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.tv.R
import com.example.tv.data.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 *
 * Displays detailed content information following the TV design.
 * When launched with EXTRA_ITEM_ID it fetches /api/info/{id} and binds:
 * - Title
 * - Description
 * - A details line: "Seasons | Episodes | TV Show"
 */
class ContentInfoActivity : ComponentActivity() {

    private lateinit var actionButtons: List<FrameLayout>
    private var currentFocusIndex = 0

    private val buttonConfigs = listOf(
        ButtonConfig("Programar", R.drawable.ic_bell),
        ButtonConfig("Reiniciar", R.drawable.ic_replay),
        ButtonConfig("Grabar", R.drawable.ic_record),
        ButtonConfig("Favorito", R.drawable.ic_favorite),
        ButtonConfig("Bloquear", R.drawable.ic_block),
        ButtonConfig("Audio y subtítulos", R.drawable.ic_audio_subtitle_button)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        setupSystemDateTime()
        setupActionButtons()

        // If launched with an item id, fetch info; otherwise keep static placeholders
        val itemId = intent.getIntExtra(EXTRA_ITEM_ID, -1)
        if (itemId != -1) {
            fetchAndBindInfo(itemId)
        } else {
            populateContentData()
        }

        // Focus first button by default
        actionButtons.firstOrNull()?.requestFocus()
    }

    private fun setupSystemDateTime() {
        val timeView = findViewById<TextView>(R.id.systemTime)
        val dateView = findViewById<TextView>(R.id.systemDate)

        val currentTime = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMM", Locale("es", "ES"))

        timeView.text = timeFormat.format(currentTime.time)
        dateView.text = dateFormat.format(currentTime.time).replace(".", "")
    }

    private fun populateContentData() {
        // Get data from intent or use defaults
        findViewById<TextView>(R.id.channelNumber).text =
            intent.getStringExtra(EXTRA_CHANNEL_NUMBER) ?: "242"

        findViewById<TextView>(R.id.channelName).text =
            intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: "TNT"

        findViewById<TextView>(R.id.programTitle).text =
            intent.getStringExtra(EXTRA_PROGRAM_TITLE) ?: "Gladiador II"

        findViewById<TextView>(R.id.metadataOriginal).text =
            intent.getStringExtra(EXTRA_PROGRAM_TITLE) ?: "Gladiator II"

        findViewById<TextView>(R.id.metadataGenres).text =
            intent.getStringExtra(EXTRA_GENRES) ?: "Acción, aventura, drama"

        findViewById<TextView>(R.id.metadataDuration).text =
            intent.getStringExtra(EXTRA_DURATION) ?: "2 h 28 min"

        findViewById<TextView>(R.id.ageRating).text =
            intent.getStringExtra(EXTRA_AGE_RATING) ?: "+ 16 Años"

        findViewById<TextView>(R.id.timeStart).text =
            intent.getStringExtra(EXTRA_TIME_START) ?: "20:00"

        findViewById<TextView>(R.id.timeEnd).text =
            intent.getStringExtra(EXTRA_TIME_END) ?: "22:20"

        findViewById<TextView>(R.id.description).text =
            intent.getStringExtra(EXTRA_DESCRIPTION) ?:
            "Lucio es obligado a entrar en el Coliseo después de que su hogar sea conquistado por los tiránicos emperadores que ahora dirigen Roma con puño de hierro. Con la ira en su corazón y el futuro del Imperio en juego, Lucio debe mirar hacia atrás para encontrar fuerza y devolver la gloria de Roma a su pueblo."
    }

    // PUBLIC_INTERFACE
    /**
     * Fetches content info from /api/info/{id} and binds the title, description, and details line.
     * Details line format: "Seasons count | Episodes count | TV Show"
     */
    private fun fetchAndBindInfo(itemId: Int) {
        val titleView = findViewById<TextView>(R.id.programTitle)
        val descriptionView = findViewById<TextView>(R.id.description)
        val detailsView = findViewById<TextView>(R.id.detailsLine)

        lifecycleScope.launch {
            try {
                val api = ApiService.create()
                val info = withContext(Dispatchers.IO) { api.getInfo(itemId) }
                titleView.text = info.title
                descriptionView.text = info.description
                detailsView.text = "${info.seasons} | ${info.total_episodes} | TV Show"
                detailsView.visibility = View.VISIBLE
            } catch (_: Throwable) {
                detailsView.text = ""
                detailsView.visibility = View.GONE
            }
        }
    }

    private fun setupActionButtons() {
        actionButtons = listOf(
            findViewById(R.id.btnSchedule),
            findViewById(R.id.btnReplay),
            findViewById(R.id.btnRecord),
            findViewById(R.id.btnFavorite),
            findViewById(R.id.btnBlock),
            findViewById(R.id.btnAudioSubtitles)
        )

        actionButtons.forEachIndexed { index, button ->
            val config = buttonConfigs[index]

            // Set icon and label
            button.findViewById<ImageView>(R.id.buttonIcon).setImageResource(config.iconRes)
            button.findViewById<TextView>(R.id.buttonLabel).text = config.label

            // Focus handling
            button.isFocusable = true
            button.isFocusableInTouchMode = true
            button.setOnFocusChangeListener { view, hasFocus ->
                animateButtonFocus(view as FrameLayout, hasFocus)
                if (hasFocus) {
                    currentFocusIndex = index
                }
            }

            // Click handling
            button.setOnClickListener {
                handleButtonAction(index)
            }
        }
    }

    private fun animateButtonFocus(button: FrameLayout, focused: Boolean) {
        val iconContainer = button.findViewById<FrameLayout>(R.id.iconContainer)
        val label = button.findViewById<TextView>(R.id.buttonLabel)

        val duration = 200L

        if (focused) {
            ObjectAnimator.ofFloat(iconContainer, "translationY", 0f, -4f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleX", 1f, 1.04f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleY", 1f, 1.04f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "elevation", 0f, 12f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(label, "alpha", 0f, 1f).apply {
                this.duration = duration
                start()
            }
        } else {
            ObjectAnimator.ofFloat(iconContainer, "translationY", iconContainer.translationY, 0f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleX", iconContainer.scaleX, 1f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleY", iconContainer.scaleY, 1f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "elevation", iconContainer.elevation, 0f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(label, "alpha", label.alpha, 0f).apply {
                this.duration = duration
                start()
            }
        }
    }

    private fun handleButtonAction(index: Int) {
        // Button press animation
        val button = actionButtons[index]
        button.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        // Placeholder actions
        when (index) {
            0 -> handleSchedule()
            1 -> handleReplay()
            2 -> handleRecord()
            3 -> handleFavorite()
            4 -> handleBlock()
            5 -> handleAudioSubtitles()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (currentFocusIndex > 0) {
                    actionButtons[currentFocusIndex - 1].requestFocus()
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (currentFocusIndex < actionButtons.size - 1) {
                    actionButtons[currentFocusIndex + 1].requestFocus()
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                handleButtonAction(currentFocusIndex)
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    // Placeholder action methods
    private fun handleSchedule() {}
    private fun handleReplay() {}
    private fun handleRecord() {}
    private fun handleFavorite() {}
    private fun handleBlock() {}
    private fun handleAudioSubtitles() {}

    private data class ButtonConfig(
        val label: String,
        val iconRes: Int
    )

    companion object {
        const val EXTRA_CHANNEL_NUMBER = "channel_number"
        const val EXTRA_CHANNEL_NAME = "channel_name"
        const val EXTRA_PROGRAM_TITLE = "program_title"
        const val EXTRA_DESCRIPTION = "description"
        const val EXTRA_GENRES = "genres"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_AGE_RATING = "age_rating"
        const val EXTRA_TIME_START = "time_start"
        const val EXTRA_TIME_END = "time_end"
        const val EXTRA_ITEM_ID = "item_id"

        // PUBLIC_INTERFACE
        /**
         * Create intent using item id to fetch and display info.
         */
        fun createIntent(
            context: Context,
            itemId: Int
        ): Intent {
            return Intent(context, ContentInfoActivity::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
            }
        }

        /**
         * PUBLIC_INTERFACE
         * Legacy intent factory for prefilled UI without fetch.
         */
        fun createIntent(
            context: Context,
            channelNumber: String = "242",
            channelName: String = "TNT",
            programTitle: String = "Gladiador II",
            description: String = "",
            genres: String = "Acción, aventura, drama",
            duration: String = "2 h 28 min",
            ageRating: String = "+ 16 Años",
            timeStart: String = "20:00",
            timeEnd: String = "22:20"
        ): Intent {
            return Intent(context, ContentInfoActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_NUMBER, channelNumber)
                putExtra(EXTRA_CHANNEL_NAME, channelName)
                putExtra(EXTRA_PROGRAM_TITLE, programTitle)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_GENRES, genres)
                putExtra(EXTRA_DURATION, duration)
                putExtra(EXTRA_AGE_RATING, ageRating)
                putExtra(EXTRA_TIME_START, timeStart)
                putExtra(EXTRA_TIME_END, timeEnd)
            }
        }
    }
}
