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
        val nameField = findViewById<EditText>(R.id.fullNameField) // keep ID same
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val btnCreateAccount = findViewById<MaterialButton>(R.id.btnCreateAccount)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Create Account
        btnCreateAccount.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API via ViewModel
            viewModel.register(name, email, password) { response, error ->
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
