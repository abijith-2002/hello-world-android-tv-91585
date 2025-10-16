package com.example.tv

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.databinding.ActivityMainBinding

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * This is the TV launcher activity that shows a single centered card with "Hello world".
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
        }
    }
}
