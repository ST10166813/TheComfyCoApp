package com.example.thecomfycoapp

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.thecomfycoapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class AuthenicationActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var logoutBtn: Button
    private lateinit var btnBack2: ImageView

    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenication)

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        btnBack2 = findViewById(R.id.btnBack2)
        val googleSignInBtn = findViewById<SignInButton>(R.id.googleSignInBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Back button
        btnBack2.setOnClickListener { finish() }

        // LOGIN
        loginBtn.setOnClickListener {
            val email = emailField.text?.toString()?.trim().orEmpty()
            val password = passwordField.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password) { response, error ->
                runOnUiThread {
                    if (error != null) {
                        Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                        return@runOnUiThread
                    }

                    // Success
                    saveToken(response?.token)
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("name", response?.userDetails?.name ?: "User")
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInBtn.setOnClickListener { signInWithGoogle() }

        // Logout (local + API)
        logoutBtn.setOnClickListener {
            viewModel.logout { response, error ->
                runOnUiThread {
                    if (error != null) {
                        Toast.makeText(this, "Logout failed: ${error.message}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, response?.get("message") ?: "Logged out", Toast.LENGTH_SHORT).show()
                        clearToken()
                    }
                }
            }
            googleSignInClient.signOut()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                saveToken("google_login")
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("name", account.displayName ?: "User")
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveToken(token: String?) {
        token?.let {
            getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("token", it)
                .apply()
        }
    }

    private fun clearToken() {
        getSharedPreferences("auth", MODE_PRIVATE)
            .edit()
            .remove("token")
            .apply()
    }
}
