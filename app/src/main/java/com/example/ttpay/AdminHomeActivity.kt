package com.example.ttpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        // Initialize the BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set a listener to track item selection in the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Check the current context to prevent re-launching the same activity
                    if (!isCurrentActivity(AdminHomeActivity::class.java)) {
                        // Open AdminHomeActivity
                        val intent = Intent(this, AdminHomeActivity::class.java)
                        startActivity(intent)
                        finish() // Close the current activity to prevent back button navigation
                    }
                }
                R.id.nav_profile -> {
                    // Check the current context to prevent re-launching the same activity
                    if (!isCurrentActivity(ProfileActivity::class.java)) {
                        // Open ProfileActivity
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish() // Close the current activity to prevent back button navigation
                    }
                }
                R.id.nav_settings -> {
                    // Check the current context to prevent re-launching the same activity
                    if (!isCurrentActivity(SettingsActivity::class.java)) {
                        // Open SettingsActivity
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent)
                        finish() // Close the current activity to prevent back button navigation
                    }
                }
                R.id.nav_logout -> {
                    // Open MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity() // Close all activities in the current task, including this one
                }
            }
            true // Return true to indicate that you have handled the item selection
        }
    }

    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        // Check if the current activity is already the one being opened
        return javaClass == activityClass
    }
}