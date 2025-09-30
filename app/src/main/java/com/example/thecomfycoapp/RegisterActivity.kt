package com.example.thecomfycoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thecomfycoapp.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val fullNameField = findViewById<EditText>(R.id.fullNameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPasswordField)
        val btnCreateAccount = findViewById<MaterialButton>(R.id.btnCreateAccount)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Back button
        btnBack.setOnClickListener {
            finish() // Go back to LoginActivity
        }

        // Create Account
        btnCreateAccount.setOnClickListener {
            val fullName = fullNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API via ViewModel
            viewModel.register(fullName, email, password) { response, error ->
                runOnUiThread {
                    if (error != null) {
                        Toast.makeText(this, "Register failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                        saveToken(response?.token)
                        finish() // Return to login
                    }
                }
            }
        }
    }

    private fun saveToken(token: String?) {
        token?.let {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit().putString("token", it).apply()
        }
    }
}
