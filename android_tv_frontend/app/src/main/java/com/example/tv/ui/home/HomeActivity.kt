package com.example.tv.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.MainActivity
import com.example.tv.R
import com.example.tv.ui.ContentInfoActivity
import com.example.tv.ui.login.LoginActivity

/**
 * PUBLIC_INTERFACE
 * HomeActivity
 * The TV Home screen with a top menu (Home, Login, Setting, My Plan), a banner,
 * multiple horizontal rails of content thumbnails, and an Available subscriptions row.
 * D-pad focus is enabled across interactive elements. Clicking Login navigates to LoginActivity,
 * and My Plan navigates to MainActivity (Hello World page).
 *
 * - Accepts no parameters.
 * - Returns no value; displays UI.
 */
class HomeActivity : AppCompatActivity() {

    // Simple dataset for rails using local thumbnails
    private val rails = listOf(
        "Top trending",
        "Continue watching",
        "Action",
        "Drama",
        "Horror",
        "Family",
        "Comedy",
        "Available subscriptions"
    )

    // Local placeholder thumbnails
    private val thumbs = listOf(
        R.drawable.thumb_1,
        R.drawable.thumb_2,
        R.drawable.thumb_3,
        R.drawable.thumb_4,
        R.drawable.thumb_5,
        R.drawable.thumb_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupTopMenu()
        setupRails()
    }

    private fun setupTopMenu() {
        val menuHome = findViewById<TextView>(R.id.menuHome)
        val menuLogin = findViewById<TextView>(R.id.menuLogin)
        val menuSetting = findViewById<TextView>(R.id.menuSetting)
        val menuMyPlan = findViewById<TextView>(R.id.menuMyPlan)

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

        // Wire navigation
        menuLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        menuMyPlan.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Initial focus on Home
        menuHome.requestFocus()
    }

    private fun setupRails() {
        val container = findViewById<LinearLayout>(R.id.railsContainer)
        rails.forEachIndexed { index, title ->
            val railView = layoutInflater.inflate(R.layout.view_rail, container, false)

            val titleView = railView.findViewById<TextView>(R.id.railTitle)
            titleView.text = title

            val scroller = railView.findViewById<HorizontalScrollView>(R.id.railScroll)
            val row = railView.findViewById<LinearLayout>(R.id.railRow)

            // Subscriptions row can use different content; reuse thumbs for now
            val items = if (title == "Available subscriptions") {
                listOf(R.drawable.sub_net, R.drawable.sub_prime, R.drawable.sub_disney, R.drawable.sub_hbo)
            } else {
                thumbs
            }

            items.forEach { resId ->
                val card = layoutInflater.inflate(R.layout.view_thumb_card, row, false)
                val img = card.findViewById<ImageView>(R.id.thumbImage)
                img.setImageResource(resId)

                // D-pad focus behavior
                card.isFocusable = true
                card.isFocusableInTouchMode = true
                card.setOnFocusChangeListener { v, hasFocus ->
                    v.animate().scaleX(if (hasFocus) 1.08f else 1.0f)
                        .scaleY(if (hasFocus) 1.08f else 1.0f)
                        .setDuration(120)
                        .start()
                    v.elevation = if (hasFocus) resources.getDimension(R.dimen.card_elevation_focused) else resources.getDimension(
                        R.dimen.card_elevation
                    )
                }

                // DPAD_CENTER handling to open ContentInfo screen
                card.setOnKeyListener { v, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && 
                        (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                        
                        // Generate sample content data based on rail and position
                        val contentTitle = when {
                            title == "Available subscriptions" -> "Premium Content"
                            title.contains("Action") -> "Action Movie"
                            title.contains("Drama") -> "Drama Series"
                            title.contains("Horror") -> "Horror Film"
                            title.contains("Comedy") -> "Comedy Show"
                            else -> "Featured Content"
                        }
                        
                        val contentDescription = "Experience thrilling entertainment with our carefully curated selection of premium content. " +
                                "This content features exceptional storytelling, outstanding performances, and high production values that will keep you engaged from start to finish."
                        
                        val intent = ContentInfoActivity.createIntent(
                            context = this@HomeActivity,
                            title = contentTitle,
                            description = contentDescription,
                            channel = if (title == "Available subscriptions") "Premium" else "TNT",
                            channelNumber = "242",
                            duration = "1 h 45 min",
                            genre = when {
                                title.contains("Action") -> "Action, Adventure"
                                title.contains("Drama") -> "Drama, Family"
                                title.contains("Horror") -> "Horror, Thriller"
                                title.contains("Comedy") -> "Comedy, Entertainment"
                                else -> "General, Entertainment"
                            },
                            rating = "+ 13 Years",
                            startTime = "20:00",
                            endTime = "21:45"
                        )
                        
                        startActivity(intent)
                        true
                    } else {
                        false
                    }
                }

                row.addView(card)
            }

            container.addView(railView)
        }
    }
}
