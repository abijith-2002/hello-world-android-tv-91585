package com.example.tv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 * A Content Info screen that matches Figma screen 35 pixel-perfectly.
 * Displays detailed metadata about a media item with full background image,
 * gradient overlay, and interactive action buttons.
 * This screen does not use Nord theme or Reddit Sans - it follows Figma styling exactly.
 *
 * @param itemTitle The title of the content item
 * @param itemDescription The description of the content item
 * @param itemChannel The channel information
 * @param itemDuration The duration of the content
 * @param itemGenre The genre of the content
 * @param itemRating The age rating
 * @param itemTime The time slot information
 */
class ContentInfoActivity : AppCompatActivity() {

    private lateinit var channelNumber: TextView
    private lateinit var channelName: TextView
    private lateinit var programTitle: TextView
    private lateinit var programSubtitle: TextView
    private lateinit var programDuration: TextView
    private lateinit var programGenre: TextView
    private lateinit var ageRating: TextView
    private lateinit var timeSlot: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var programDescription: TextView
    private lateinit var currentTime: TextView
    private lateinit var currentDate: TextView
    private lateinit var backgroundImage: ImageView

    // Action buttons
    private lateinit var programButton: Button
    private lateinit var replayButton: Button
    private lateinit var recordButton: Button
    private lateinit var playButton: Button
    private lateinit var infoButton: Button
    private lateinit var subtitlesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        initializeViews()
        populateContent()
        setupFocusBehavior()
        setupActionButtons()
    }

    private fun initializeViews() {
        backgroundImage = findViewById(R.id.backgroundImage)
        channelNumber = findViewById(R.id.channelNumber)
        channelName = findViewById(R.id.channelName)
        programTitle = findViewById(R.id.programTitle)
        programSubtitle = findViewById(R.id.programSubtitle)
        programDuration = findViewById(R.id.programDuration)
        programGenre = findViewById(R.id.programGenre)
        ageRating = findViewById(R.id.ageRating)
        timeSlot = findViewById(R.id.timeSlot)
        startTime = findViewById(R.id.startTime)
        endTime = findViewById(R.id.endTime)
        programDescription = findViewById(R.id.programDescription)
        currentTime = findViewById(R.id.currentTime)
        currentDate = findViewById(R.id.currentDate)

        programButton = findViewById(R.id.programButton)
        replayButton = findViewById(R.id.replayButton)
        recordButton = findViewById(R.id.recordButton)
        playButton = findViewById(R.id.playButton)
        infoButton = findViewById(R.id.infoButton)
        subtitlesButton = findViewById(R.id.subtitlesButton)
    }

    private fun populateContent() {
        // Get data from intent or use default Gladiator II content
        val itemTitle = intent.getStringExtra(EXTRA_TITLE) ?: "Gladiador II"
        val itemDescription = intent.getStringExtra(EXTRA_DESCRIPTION) ?: 
            "Lucio es obligado a entrar en el Coliseo después de que su hogar sea conquistado por los tiránicos emperadores que ahora dirigen Roma con puño de hierro. Con la ira en su corazón y el futuro del Imperio en juego, Lucio debe mirar hacia atrás para encontrar fuerza y devolver la gloria de Roma a su pueblo."
        val itemChannel = intent.getStringExtra(EXTRA_CHANNEL) ?: "TNT"
        val itemChannelNumber = intent.getStringExtra(EXTRA_CHANNEL_NUMBER) ?: "242"
        val itemDuration = intent.getStringExtra(EXTRA_DURATION) ?: "2 h 28 min"
        val itemGenre = intent.getStringExtra(EXTRA_GENRE) ?: "Acción, aventura, drama"
        val itemRating = intent.getStringExtra(EXTRA_RATING) ?: "+ 16 Años"
        val itemStartTime = intent.getStringExtra(EXTRA_START_TIME) ?: "20:00"
        val itemEndTime = intent.getStringExtra(EXTRA_END_TIME) ?: "22:20"

        // Set background image
        backgroundImage.setImageResource(R.drawable.content_info_bg)

        // Populate text fields
        channelNumber.text = itemChannelNumber
        channelName.text = itemChannel
        programTitle.text = itemTitle
        programSubtitle.text = "Gladiator II"
        programDuration.text = itemDuration
        programGenre.text = itemGenre
        ageRating.text = itemRating
        timeSlot.text = "MÁS TARDE"
        startTime.text = itemStartTime
        endTime.text = itemEndTime
        programDescription.text = itemDescription

        // Set current time and date
        currentTime.text = "20:44"
        currentDate.text = "7 abr."
    }

    private fun setupFocusBehavior() {
        val focusScaler = View.OnFocusChangeListener { v, hasFocus ->
            v.animate()
                .scaleX(if (hasFocus) 1.05f else 1.0f)
                .scaleY(if (hasFocus) 1.05f else 1.0f)
                .setDuration(120)
                .start()
        }

        // Apply focus behavior to all buttons
        arrayOf(programButton, replayButton, recordButton, playButton, infoButton, subtitlesButton).forEach { button ->
            button.isFocusable = true
            button.isFocusableInTouchMode = true
            button.onFocusChangeListener = focusScaler
        }

        // Set initial focus to program button
        programButton.requestFocus()
    }

    private fun setupActionButtons() {
        programButton.setOnClickListener {
            // Handle program action
        }

        replayButton.setOnClickListener {
            // Handle replay action
        }

        recordButton.setOnClickListener {
            // Handle record action
        }

        playButton.setOnClickListener {
            // Handle play action
        }

        infoButton.setOnClickListener {
            // Handle info action
        }

        subtitlesButton.setOnClickListener {
            // Handle subtitles action
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_CHANNEL = "extra_channel"
        private const val EXTRA_CHANNEL_NUMBER = "extra_channel_number"
        private const val EXTRA_DURATION = "extra_duration"
        private const val EXTRA_GENRE = "extra_genre"
        private const val EXTRA_RATING = "extra_rating"
        private const val EXTRA_START_TIME = "extra_start_time"
        private const val EXTRA_END_TIME = "extra_end_time"

        /**
         * PUBLIC_INTERFACE
         * createIntent
         * Create an Intent to open the ContentInfoActivity with item metadata.
         * @param context The context used to create the intent.
         * @param title The title of the content item.
         * @param description The description of the content item.
         * @param channel The channel name.
         * @param channelNumber The channel number.
         * @param duration The duration of the content.
         * @param genre The genre of the content.
         * @param rating The age rating.
         * @param startTime The start time.
         * @param endTime The end time.
         * @return Intent to start ContentInfoActivity.
         */
        fun createIntent(
            context: Context,
            title: String = "Gladiador II",
            description: String = "Lucio es obligado a entrar en el Coliseo después de que su hogar sea conquistado por los tiránicos emperadores que ahora dirigen Roma con puño de hierro.",
            channel: String = "TNT",
            channelNumber: String = "242",
            duration: String = "2 h 28 min",
            genre: String = "Acción, aventura, drama",
            rating: String = "+ 16 Años",
            startTime: String = "20:00",
            endTime: String = "22:20"
        ): Intent {
            return Intent(context, ContentInfoActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_CHANNEL, channel)
                putExtra(EXTRA_CHANNEL_NUMBER, channelNumber)
                putExtra(EXTRA_DURATION, duration)
                putExtra(EXTRA_GENRE, genre)
                putExtra(EXTRA_RATING, rating)
                putExtra(EXTRA_START_TIME, startTime)
                putExtra(EXTRA_END_TIME, endTime)
            }
        }
    }
}
