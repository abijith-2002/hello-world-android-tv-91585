package com.example.tv.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R

/**
 * PUBLIC_INTERFACE
 * LoginActivity
 * Simple login form used for demonstration.
 * Validates username/email and password, showing toast messages from string resources.
 * Accepts no parameters and returns no value.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username: EditText = findViewById(R.id.input_username)
        val password: EditText = findViewById(R.id.input_password)
        val submit: Button = findViewById(R.id.action_sign_in)

        submit.setOnClickListener {
            val user = username.text?.toString().orEmpty()
            val pass = password.text?.toString().orEmpty()

            when {
                user.isBlank() -> {
                    Toast.makeText(this, getString(R.string.error_username_required), Toast.LENGTH_SHORT).show()
                }
                pass.isBlank() -> {
                    Toast.makeText(this, getString(R.string.error_password_required), Toast.LENGTH_SHORT).show()
                }
                pass.length < 6 -> {
                    Toast.makeText(this, getString(R.string.error_password_length), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
