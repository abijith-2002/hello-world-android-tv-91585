package com.example.tv.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * LoginActivity
 * A simple TV-friendly login screen with Nord dark theme, D-pad focus handling,
 * visible focus states, and basic client-side validation.
 *
 * Intent extras: none.
 * Returns: none. Shows UI and displays validation toast on submit.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var submitBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameField = findViewById(R.id.inputUsername)
        passwordField = findViewById(R.id.inputPassword)
        submitBtn = findViewById(R.id.btnSubmit)
        backBtn = findViewById(R.id.btnBack)
        errorText = findViewById(R.id.errorText)

        // Ensure inputs are focusable by D-pad
        usernameField.isFocusable = true
        usernameField.isFocusableInTouchMode = true
        passwordField.isFocusable = true
        passwordField.isFocusableInTouchMode = true
        submitBtn.isFocusable = true
        submitBtn.isFocusableInTouchMode = true
        backBtn.isFocusable = true
        backBtn.isFocusableInTouchMode = true

        // Input types for TV keyboards if present
        usernameField.imeOptions = EditorInfo.IME_ACTION_NEXT
        passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordField.imeOptions = EditorInfo.IME_ACTION_DONE

        // Focus visual scaling for TV best practices
        val focusScaler = OnFocusChangeListener { v, hasFocus ->
            v.animate().scaleX(if (hasFocus) 1.03f else 1.0f)
                .scaleY(if (hasFocus) 1.03f else 1.0f)
                .setDuration(120)
                .start()
        }
        usernameField.onFocusChangeListener = focusScaler
        passwordField.onFocusChangeListener = focusScaler
        submitBtn.onFocusChangeListener = focusScaler
        backBtn.onFocusChangeListener = focusScaler

        // Nav back
        backBtn.setOnClickListener { finish() }

        // Submit via button
        submitBtn.setOnClickListener { submit() }

        // Submit via keyboard done
        passwordField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                submit()
                true
            } else {
                false
            }
        }

        // Initial focus to username
        usernameField.requestFocus()
    }

    private fun validate(username: String, password: String): String? {
        if (username.isBlank()) return getString(R.string.error_username_required)
        // Very simple email/username check: accept if contains '@' or length >= 3
        val isEmail = username.contains("@") && username.contains(".")
        val isUsername = username.length >= 3
        if (!isEmail && !isUsername) return getString(R.string.error_username_invalid)

        if (password.isBlank()) return getString(R.string.error_password_required)
        if (password.length < 4) return getString(R.string.error_password_length)
        return null
    }

    private fun submit() {
        val username = usernameField.text?.toString()?.trim().orEmpty()
        val password = passwordField.text?.toString()?.trim().orEmpty()

        val error = validate(username, password)
        if (error != null) {
            errorText.visibility = View.VISIBLE
            errorText.text = error
            // move focus to the error for accessibility, then back to the first invalid input
            errorText.sendAccessibilityEvent(View.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
            if (username.isBlank() || (!username.contains("@") && username.length < 3)) {
                usernameField.requestFocus()
            } else {
                passwordField.requestFocus()
            }
            return
        } else {
            errorText.visibility = View.GONE
        }

        // Placeholder sign-in action
        Toast.makeText(this, getString(R.string.sign_in_success, username), Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity and finish Login so Back doesn't return here
        val intent = Intent(this, com.example.tv.MainActivity::class.java).apply {
            // Clear any intermediate activities and start main as a fresh task top
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        /**
         * PUBLIC_INTERFACE
         * createIntent
         * Create an Intent to open the LoginActivity.
         * @param context The context used to create the intent.
         * @return Intent to start LoginActivity.
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
