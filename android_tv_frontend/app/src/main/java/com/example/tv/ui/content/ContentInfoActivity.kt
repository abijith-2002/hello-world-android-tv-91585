package com.example.tv.ui.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
 * Displays detailed content information screen matching design with background, metadata,
 * and system date/time. Action buttons are unavailable if layout/view_action_button is missing.
 *
 * Features:
 * - Full-screen background with overlay
 * - Metadata and description
 * - System date/time in top-right corner
 * 
 * Intent extras as before.
 */
class ContentInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        setupSystemDateTime()
        populateContentData()
        // Action buttons are removed - Compose/TV Compose preferred.
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
            intent.getStringExtra(EXTRA_DESCRIPTION)
                ?: "Lucio es obligado a entrar en el Coliseo después de que su hogar sea conquistado por los tiránicos emperadores que ahora dirigen Roma con puño de hierro. Con la ira en su corazón y el futuro del Imperio en juego, Lucio debe mirar hacia atrás para encontrar fuerza y devolver la gloria de Roma a su pueblo."
    }

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
