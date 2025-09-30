package com.example.thecomfycoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        // Get the user name passed from AuthenticationActivity
        val userName = intent.getStringExtra("userName") ?: "User"

        tvWelcome.text = "Welcome, $userName!"
    }
}
