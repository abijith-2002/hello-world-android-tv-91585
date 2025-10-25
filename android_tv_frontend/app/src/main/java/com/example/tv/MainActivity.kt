package com.example.tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * Minimal activity hosting a simple layout; used as a placeholder entry and navigation target.
 * Accepts no parameters and returns no value.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
