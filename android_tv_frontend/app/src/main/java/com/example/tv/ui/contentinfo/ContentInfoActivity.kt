package com.example.tv.ui.contentinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R
import com.example.tv.model.ContentItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 * Displays detailed content information matching the provided design image pixel-perfectly.
 * Uses a dedicated theme separate from the global Nord theme.
 * 
 * Intent extras:
 * - EXTRA_CONTENT_ITEM: ContentItem (Parcelable) with all metadata
 * 
 * Returns: none. Displays UI and handles back navigation.
 */
class ContentInfoActivity : AppCompatActivity() {

    private lateinit var backgroundImage: ImageView
    private lateinit var channelNumber: TextView
    private lateinit var channelName: TextView
    private lateinit var contentTitle: TextView
    private lateinit var subtitle: TextView
    private lateinit var duration: TextView
    private lateinit var genres: TextView
    private lateinit var ageRating: TextView
    private lateinit var timeLabel: TextView
    private lateinit var timeSlot: TextView
    private lateinit var description: TextView
    private lateinit var systemTime: TextView
    private lateinit var systemDate: TextView

    private val actionButtons = mutableListOf<View>()
    private val actionIcons = mutableListOf<ImageView>()
    private val actionLabels = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        initializeViews()
        
        val contentItem = intent.getParcelableExtra<ContentItem>(EXTRA_CONTENT_ITEM)
        if (contentItem != null) {
            populateContent(contentItem)
        } else {
            // Fallback with sample data
            populateSampleContent()
        }

        setupSystemDateTime()
        setupActionButtons()
        setupFocusHandling()
    }

    private fun initializeViews() {
        backgroundImage = findViewById(R.id.backgroundImage)
        channelNumber = findViewById(R.id.channelNumber)
        channelName = findViewById(R.id.channelName)
        contentTitle = findViewById(R.id.contentTitle)
        subtitle = findViewById(R.id.subtitle)
        duration = findViewById(R.id.duration)
        genres = findViewById(R.id.genres)
        ageRating = findViewById(R.id.ageRating)
        timeLabel = findViewById(R.id.timeLabel)
        timeSlot = findViewById(R.id.timeSlot)
        description = findViewById(R.id.description)
        systemTime = findViewById(R.id.systemTime)
        systemDate = findViewById(R.id.systemDate)

        // Collect action buttons
        val buttonIds = listOf(
            R.id.actionButton1,
            R.id.actionButton2,
            R.id.actionButton3,
            R.id.actionButton4,
            R.id.actionButton5,
            R.id.actionButton6
        )

        buttonIds.forEach { id ->
            findViewById<View>(id)?.let { button ->
                actionButtons.add(button)
                button.findViewById<ImageView>(R.id.actionIcon)?.let { actionIcons.add(it) }
                button.findViewById<TextView>(R.id.actionLabel)?.let { actionLabels.add(it) }
            }
        }
    }

    private fun populateContent(item: ContentItem) {
        // Set background image - load Gladiator II background from assets
        loadBackgroundFromAssets()

        // Channel info
        channelNumber.text = item.channelNumber
        channelName.text = item.channelName

        // Title
        contentTitle.text = item.title

        // Metadata
        if (item.subtitle.isNotEmpty()) {
            subtitle.text = item.subtitle
            subtitle.visibility = View.VISIBLE
        } else {
            subtitle.visibility = View.GONE
        }

        if (item.duration.isNotEmpty()) {
            duration.text = item.duration
            duration.visibility = View.VISIBLE
        } else {
            duration.visibility = View.GONE
        }

        if (item.genres.isNotEmpty()) {
            genres.text = item.genres
            genres.visibility = View.VISIBLE
        } else {
            genres.visibility = View.GONE
        }

        if (item.ageRating.isNotEmpty()) {
            ageRating.text = item.ageRating
            ageRating.visibility = View.VISIBLE
        } else {
            ageRating.visibility = View.GONE
        }

        // Time info
        if (item.timeLabel.isNotEmpty()) {
            timeLabel.text = item.timeLabel
            timeLabel.visibility = View.VISIBLE
        } else {
            timeLabel.visibility = View.GONE
        }

        if (item.timeSlot.isNotEmpty()) {
            timeSlot.text = item.timeSlot
            timeSlot.visibility = View.VISIBLE
        } else {
            timeSlot.visibility = View.GONE
        }

        // Description
        description.text = item.description
    }

    private fun populateSampleContent() {
        // Sample content matching the design image
        channelNumber.text = "242"
        channelName.text = "TNT"
        contentTitle.text = "Gladiador II"
        subtitle.text = "Gladiator II"
        duration.text = "2 h 28 min"
        genres.text = "Acción, aventura, drama"
        ageRating.text = "+ 16 Años"
        timeLabel.text = "MÁS TARDE"
        timeSlot.text = "20:00 - 22:20"
        description.text = "Lucio es obligado a entrar en el Coliseo después de que su hogar sea conquistado " +
                "por los tiránicos emperadores que ahora dirigen Roma con puño de hierro. Con la ira en su " +
                "corazón y el futuro del Imperio en juego, Lucio debe mirar hacia atrás para encontrar fuerza " +
                "y devolver la gloria de Roma a su pueblo."
        
        // Load the Gladiator II background image from assets
        loadBackgroundFromAssets()
    }

    private fun loadBackgroundFromAssets() {
        try {
            assets.open("content_info_bg.jpg").use { inputStream ->
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                backgroundImage.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            // Fallback to a default drawable if asset loading fails
            backgroundImage.setImageResource(R.drawable.thumb_1)
        }
    }

    private fun setupSystemDateTime() {
        val currentTime = Date()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        
        systemTime.text = timeFormat.format(currentTime)
        systemDate.text = dateFormat.format(currentTime).replace(".", "")
    }

    private fun setupActionButtons() {
        // Configure action buttons with icons and labels
        val actionConfigs = listOf(
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_schedule)),
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_replay)),
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_play)),
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_record)),
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_info)),
            ActionConfig(R.drawable.ic_launcher_foreground, getString(R.string.action_audio_subtitles))
        )

        actionConfigs.forEachIndexed { index, config ->
            if (index < actionIcons.size && index < actionLabels.size) {
                actionIcons[index].setImageResource(config.iconRes)
                actionLabels[index].text = config.label
            }
        }
    }

    private fun setupFocusHandling() {
        // Set up focus scaling for action buttons
        val focusScaler = View.OnFocusChangeListener { v, hasFocus ->
            v.animate()
                .scaleX(if (hasFocus) 1.05f else 1.0f)
                .scaleY(if (hasFocus) 1.05f else 1.0f)
                .setDuration(120)
                .start()
        }

        actionButtons.forEach { button ->
            button.isFocusable = true
            button.isFocusableInTouchMode = true
            button.onFocusChangeListener = focusScaler
        }

        // Set initial focus to first action button
        if (actionButtons.isNotEmpty()) {
            actionButtons[0].requestFocus()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private data class ActionConfig(val iconRes: Int, val label: String)

    companion object {
        private const val EXTRA_CONTENT_ITEM = "extra_content_item"

        /**
         * PUBLIC_INTERFACE
         * createIntent
         * Create an Intent to open ContentInfoActivity with the specified content item.
         * @param context The context used to create the intent.
         * @param contentItem The content item to display.
         * @return Intent to start ContentInfoActivity.
         */
        fun createIntent(context: Context, contentItem: ContentItem): Intent {
            return Intent(context, ContentInfoActivity::class.java).apply {
                putExtra(EXTRA_CONTENT_ITEM, contentItem)
            }
        }
    }
}
