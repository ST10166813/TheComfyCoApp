package com.example.thecomfycoapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

       val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        //Get the user name passed from AuthenticationActivity
        val fullName = intent.getStringExtra("name") ?: "User"
        tvWelcome.text = "Welcome, $fullName!"


    }
}

