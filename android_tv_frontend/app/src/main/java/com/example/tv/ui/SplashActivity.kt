package com.example.tv.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.MainActivity
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * SplashActivity shows a simple splash screen, then navigates to MainActivity.
 *
 * Behavior:
 * - Displays R.layout.activity_splash.
 * - Immediately starts MainActivity and finishes itself.
 *
 * Parameters: None
 * Returns: None
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
