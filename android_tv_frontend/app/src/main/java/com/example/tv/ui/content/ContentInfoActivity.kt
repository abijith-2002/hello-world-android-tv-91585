package com.example.tv.ui.content

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.tv.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 *
 * Displays detailed content information screen matching design with background, metadata, and action buttons.
 *
 * Features:
 * - Full-screen background with overlay
 * - Metadata and description
 * - 6 focusable action buttons with focus states
 * - System date/time in top-right corner
 * - D-PAD navigation (left/right) across buttons
 *
 * Intent extras:
 * - EXTRA_CHANNEL_NUMBER: String - Channel number (default "242")
 * - EXTRA_CHANNEL_NAME: String - Channel name (default "TNT")
 * - EXTRA_PROGRAM_TITLE: String - Program title (default "Gladiador II")
 * - EXTRA_DESCRIPTION: String - Program description
 * - EXTRA_GENRES: String - Genres (default "Acción, aventura, drama")
 * - EXTRA_DURATION: String - Duration (default "2 h 28 min")
 * - EXTRA_AGE_RATING: String - Age rating (default "+ 16 Años")
 * - EXTRA_TIME_START: String - Start time (default "20:00")
 * - EXTRA_TIME_END: String - End time (default "22:20")
 *
 * @param None
 * @return Displays UI and handles user interaction
 */
class ContentInfoActivity : ComponentActivity() {

    // Root containers for action buttons; type-neutral to avoid ClassCastException with layout changes
    private lateinit var actionButtons: List<ViewGroup>
    private var currentFocusIndex = 0

    // Defer resource access until onCreate() to avoid NPE before context is ready
    private lateinit var buttonConfigs: List<ButtonConfig>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        // Initialize button configurations now that resources are available
        buttonConfigs = listOf(
            ButtonConfig(getString(R.string.button_play), R.drawable.ic_play),
            ButtonConfig(getString(R.string.button_schedule), R.drawable.ic_bell),
            ButtonConfig(getString(R.string.button_record), R.drawable.ic_record),
            ButtonConfig(getString(R.string.button_favorite), R.drawable.ic_favorite),
            ButtonConfig(getString(R.string.button_block), R.drawable.ic_block),
            ButtonConfig(getString(R.string.button_audio_subtitles), R.drawable.ic_audio_subtitle_button)
        )

        setupSystemDateTime()
        populateContentData()
        setupActionButtons()

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

    private fun setupActionButtons() {
        actionButtons = listOf(
            findViewById(R.id.btnPlay),
            findViewById(R.id.btnSchedule),
            findViewById(R.id.btnRecord),
            findViewById(R.id.btnFavorite),
            findViewById(R.id.btnBlock),
            findViewById(R.id.btnAudioSubtitles)
        )

        actionButtons.forEachIndexed { index, button ->
            val config = buttonConfigs[index]

            // Set icon (icon-only design; no text label)
            // Support both standard and "first" variant layouts which have different IDs.
            val iconView = button.findViewById<ImageView>(R.id.buttonIcon)
                ?: button.findViewById(R.id.buttonIconFirst)
            iconView?.setImageResource(config.iconRes)

            // Setup focus handling
            button.isFocusable = true
            button.isFocusableInTouchMode = true
            button.setOnFocusChangeListener { view, hasFocus ->
                val vg = view as? ViewGroup ?: return@setOnFocusChangeListener
                animateButtonFocus(vg, hasFocus)
                if (hasFocus) {
                    currentFocusIndex = index
                }
            }

            // Setup click handling
            button.setOnClickListener {
                handleButtonAction(index)
            }
        }
    }

    private fun animateButtonFocus(buttonRoot: ViewGroup, focused: Boolean) {
        val iconContainer = buttonRoot.findViewById<View>(R.id.iconContainer)
            ?: buttonRoot.findViewById(R.id.iconContainerFirst)
        val duration = 200L

        if (iconContainer == null) return

        if (focused) {
            ObjectAnimator.ofFloat(iconContainer, "translationY", 0f, -2f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleX", 1f, 1.02f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "scaleY", 1f, 1.02f).apply {
                this.duration = duration
                start()
            }
            ObjectAnimator.ofFloat(iconContainer, "elevation", 0f, 8f).apply {
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

        // Handle specific button actions
        when (index) {
            0 -> handlePlay()        // Play
            1 -> handleSchedule()
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

    private fun handleSchedule() {
        // TODO: Implement schedule/reminder functionality
    }

    // PUBLIC_INTERFACE
    private fun handlePlay() {
        /** Performs GET /api/play, expects JSON {"url": "<mediaUrl>"}. Validates and launches PlayerActivity with that URL. */
        val fallbackUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"

        // Build absolute endpoint from NetworkConfig base
        val base = com.example.tv.data.api.NetworkConfig.getBaseUrl().trimEnd('/')
        val endpoint = "$base/api/play"

        // Prepare OkHttp request
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                level = if (com.example.tv.BuildConfig.DEBUG)
                    okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                else
                    okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
            })
            .build()

        val request = okhttp3.Request.Builder()
            .url(endpoint)
            .get()
            .build()

        // Execute async to avoid blocking UI
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                runOnUiThread {
                    android.util.Log.e("ContentInfoActivity", "GET /api/play failed", e)
                    android.widget.Toast.makeText(
                        this@ContentInfoActivity,
                        "Network error starting playback",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    // Optional fallback: try known demo URL so UX isn't a dead end
                    startActivity(com.example.tv.ui.player.PlayerActivity.createIntent(this@ContentInfoActivity, fallbackUrl))
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val bodyStr = response.body?.string()?.trim().orEmpty()
                var parsedUrl: String? = null
                if (response.isSuccessful) {
                    parsedUrl = extractUrlFromResponse(bodyStr)
                }
                val validUrl = parsedUrl?.takeIf { isValidHttpUrl(it) }
                runOnUiThread {
                    if (!response.isSuccessful) {
                        android.util.Log.w("ContentInfoActivity", "GET /api/play HTTP ${response.code}: $bodyStr")
                        android.widget.Toast.makeText(
                            this@ContentInfoActivity,
                            "Server error: ${response.code}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        startActivity(com.example.tv.ui.player.PlayerActivity.createIntent(this@ContentInfoActivity, fallbackUrl))
                    } else if (validUrl.isNullOrBlank()) {
                        android.util.Log.w("ContentInfoActivity", "Missing or invalid 'url' in response: $bodyStr")
                        android.widget.Toast.makeText(
                            this@ContentInfoActivity,
                            "Invalid media URL from server",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        startActivity(com.example.tv.ui.player.PlayerActivity.createIntent(this@ContentInfoActivity, fallbackUrl))
                    } else {
                        startActivity(com.example.tv.ui.player.PlayerActivity.createIntent(this@ContentInfoActivity, validUrl))
                    }
                }
                response.close()
            }
        })
    }

    private fun extractUrlFromResponse(resp: String): String? {
        // Parse JSON: {"url":"..."} using Moshi top-level model, else if body is a plain URL return it.
        try {
            if (resp.startsWith("{")) {
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(com.example.tv.network.PlayResp::class.java)
                val parsed = adapter.fromJson(resp)
                parsed?.url?.let { urlVal ->
                    if (!urlVal.isNullOrBlank()) return urlVal
                }
            }
        } catch (t: Throwable) {
            android.util.Log.w("ContentInfoActivity", "Failed to parse /api/play JSON", t)
        }
        // If JSON parse didn't yield url, treat body as a possible direct URL string
        return resp.takeIf { isValidHttpUrl(it) }
    }

    private fun isValidHttpUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return url.startsWith("http://") || url.startsWith("https://")
    }

    private fun handleRecord() {
        // TODO: Implement record functionality
    }

    private fun handleFavorite() {
        // TODO: Implement favorite/like functionality
    }

    private fun handleBlock() {
        // TODO: Implement block/parental control functionality
    }

    private fun handleAudioSubtitles() {
        // TODO: Implement audio/subtitle selection
    }

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

        /**
         * PUBLIC_INTERFACE
         * createIntent
         *
         * Factory method to create an Intent to launch ContentInfoActivity with content metadata.
         *
         * @param context The context from which to launch the activity
         * @param channelNumber Channel number to display
         * @param channelName Channel name to display
         * @param programTitle Program/movie title
         * @param description Program description (max 3 lines)
         * @param genres Comma-separated genres
         * @param duration Duration string (e.g., "2 h 28 min")
         * @param ageRating Age rating (e.g., "+ 16 Años")
         * @param timeStart Start time (e.g., "20:00")
         * @param timeEnd End time (e.g., "22:20")
         * @return Intent configured to launch ContentInfoActivity
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
