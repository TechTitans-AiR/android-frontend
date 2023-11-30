package com.example.ttpay.model

// NavigationHandler.kt
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.navigationBar.activities.MainActivity
import com.example.ttpay.navigationBar.activities.ProfileActivity
import com.example.ttpay.R
import com.example.ttpay.navigationBar.activities.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationHandler(private val activity: AppCompatActivity) {

    private lateinit var bottomNavigationView: BottomNavigationView

    fun setupWithBottomNavigation(bottomNavigationView: BottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> navigateToActivity(AdminHomeActivity::class.java)
                R.id.nav_profile -> navigateToActivity(ProfileActivity::class.java)
                R.id.nav_settings -> navigateToActivity(SettingsActivity::class.java)
                R.id.nav_logout -> navigateToMainActivity()
            }
            true
        }

        // Highlight the initially selected item
        highlightSelectedItem()
    }

    private fun navigateToActivity(targetActivity: Class<*>) {
        if (!isCurrentActivity(targetActivity)) {
            val intent = Intent(activity, targetActivity)
            activity.startActivity(intent)

            // Do not finish the current activity immediately

            // Highlight the selected item in BottomNavigationView
            highlightSelectedItem()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finishAffinity()
    }

    private fun isCurrentActivity(targetActivity: Class<*>): Boolean {
        return activity.javaClass == targetActivity
    }

    private fun highlightSelectedItem() {
        // Highlight the selected item based on the current activity
        when {
            isCurrentActivity(AdminHomeActivity::class.java) -> bottomNavigationView.selectedItemId = R.id.nav_home
            isCurrentActivity(ProfileActivity::class.java) -> bottomNavigationView.selectedItemId = R.id.nav_profile
            isCurrentActivity(SettingsActivity::class.java) -> bottomNavigationView.selectedItemId = R.id.nav_settings
            // Add more cases for other activities if needed
        }
    }
}
