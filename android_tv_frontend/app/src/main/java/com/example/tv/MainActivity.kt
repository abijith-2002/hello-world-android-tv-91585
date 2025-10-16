package com.example.tv

import android.os.Bundle
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

        // Set text and request initial focus for D-pad navigation.
        // Note: Theme sets unfocusedAlpha=1.0 to avoid dimming text on TV previews.
        binding.titleText.text = getString(R.string.hello_world)
        binding.helloCard.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
    }
}
