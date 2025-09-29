package com.example.thecomfycoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thecomfycoapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var statusText: TextView
    private lateinit var logoutBtn: Button

    private val RC_SIGN_IN = 1001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI references
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val googleSignInBtn = findViewById<SignInButton>(R.id.googleSignInBtn)
        logoutBtn = findViewById<Button>(R.id.logoutBtn)
        statusText = findViewById<TextView>(R.id.statusText)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // 🔹 Register
        registerBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val name = email.substringBefore("@") // simple name from email

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password) { response, error ->
                runOnUiThread {
                    if (error != null) {
                        statusText.text = "Register failed: ${error.message}"
                    } else {
                        statusText.text = "Registered ID: ${response?.userId}"
                        saveToken(response?.token)
                    }
                }
            }
        }

        // 🔹 Login
        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password) { response, error ->
                runOnUiThread {
                    if (error != null) {
                        statusText.text = "Login failed: ${error.message}"
                    } else {
                        statusText.text = "Logged in as: ${response?.userDetails?.name}"
                        saveToken(response?.token)
                    }
                }
            }
        }

        // 🔹 Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 🔹 Google Sign-In button click
        googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        // 🔹 Logout button
        logoutBtn.setOnClickListener {
            // Logout from backend
            viewModel.logout { response, error ->
                runOnUiThread {
                    if (error != null) {
                        statusText.text = "Logout failed: ${error.message}"
                    } else {
                        statusText.text = response?.get("message") ?: "Logged out "
                        clearToken()
                    }
                }
            }

            // Logout from Google if signed in
            googleSignInClient.signOut().addOnCompleteListener {
                statusText.text = "Not logged in"
            }
        }
    }

    // 🔹 Google Sign-In
    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val userName = account.displayName
                val userEmail = account.email
                statusText.text = "Logged in as: $userName ($userEmail)"
                Toast.makeText(this, "Welcome $userName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
            statusText.text = "Google Sign-In failed"
        }
    }

    // Save JWT token locally
    private fun saveToken(token: String?) {
        token?.let {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit().putString("token", it).apply()
        }
    }

    private fun clearToken() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().remove("token").apply()
    }
}
