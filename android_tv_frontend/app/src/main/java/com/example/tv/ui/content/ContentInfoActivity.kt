package com.example.tv.ui.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * ContentInfoActivity
 * Simple details screen to display selected program information.
 *
 * Parameters via Intent extras:
 * - EXTRA_TITLE (String): Program title to display.
 * - EXTRA_DESCRIPTION (String): Description text.
 * - EXTRA_GENRES (String): Genres label.
 *
 * Returns: None
 */
class ContentInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_info)

        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val genres = intent.getStringExtra(EXTRA_GENRES).orEmpty()

        findViewById<TextView>(R.id.content_title)?.text = title
        findViewById<TextView>(R.id.content_description)?.text = description
        findViewById<TextView>(R.id.content_genres)?.text = genres
    }

    companion object {
        const val EXTRA_TITLE = "program_title"
        const val EXTRA_DESCRIPTION = "program_description"
        const val EXTRA_GENRES = "program_genres"

        // PUBLIC_INTERFACE
        /**
         * Create intent for launching ContentInfoActivity with details.
         */
        fun createIntent(context: Context, programTitle: String, description: String, genres: String): Intent {
            return Intent(context, ContentInfoActivity::class.java).apply {
                putExtra(EXTRA_TITLE, programTitle)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_GENRES, genres)
            }
        }
    }
}
