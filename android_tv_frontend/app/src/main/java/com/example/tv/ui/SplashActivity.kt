package com.example.tv.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R
import com.example.tv.ui.home.HomeActivity

/**
 * PUBLIC_INTERFACE
 * SplashActivity
 * A simple launcher splash screen that displays the app brand "MyTV" for 3 seconds,
 * then navigates to HomeActivity.
 *
 * - Accepts no parameters.
 * - Returns no value; transitions to HomeActivity.
 */
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var launched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ensure focusable to accept D-pad and provide clean splash
        findViewById<View>(R.id.splashRoot)?.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

        // Navigate to Home after 3 seconds
        handler.postDelayed({
            if (!launched) {
                launched = true
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
