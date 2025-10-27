package com.example.tv

// TV app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.databinding.ActivityMainBinding
import com.example.tv.ui.login.LoginActivity

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * This is the TV launcher activity that shows a single centered card with "Hello world"
 * and allows navigation to a Login screen.
 * - Accepts no parameters.
 * - Returns no value; displays UI.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure content is visible and not clipped
        binding.titleText.apply {
            text = getString(R.string.hello_world)
            alpha = 1.0f
            visibility = View.VISIBLE
        }

        // Card visibility and focus behavior for TV
        binding.helloCard.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            alpha = 1.0f
            visibility = View.VISIBLE
            requestFocus()

            // Act as a button to open Login
            setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            // Add simple scale focus feedback
            setOnFocusChangeListener { v, hasFocus ->
                v.animate().scaleX(if (hasFocus) 1.03f else 1.0f)
                    .scaleY(if (hasFocus) 1.03f else 1.0f)
                    .setDuration(120)
                    .start()
            }
        }
    }
}
