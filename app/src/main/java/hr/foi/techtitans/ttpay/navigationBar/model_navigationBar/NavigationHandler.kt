package hr.foi.techtitans.ttpay.navigationBar.model_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MainActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.ProfileActivity
import hr.foi.techtitans.ttpay.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity

class NavigationHandler(private val activity: AppCompatActivity, private var loggedInUser: LoggedInUser) {

    private lateinit var bottomNavigationView: BottomNavigationView

    fun setupWithBottomNavigation(bottomNavigationView: BottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    when (loggedInUser.role) {
                        "admin" -> navigateToActivity(AdminHomeActivity::class.java)
                        "merchant" -> navigateToActivity(MerchantHomeActivity::class.java)
                    }
                }
                R.id.nav_profile -> navigateToActivity(ProfileActivity::class.java)
                R.id.nav_logout -> navigateToMainActivity()
            }
            true
        }
        highlightSelectedItem()
    }

    private fun navigateToActivity(targetActivity: Class<*>) {
        if (!isCurrentActivity(targetActivity)) {
            val intent = Intent(activity, targetActivity)
            intent.putExtra("loggedInUser", loggedInUser)
            activity.startActivity(intent)
            highlightSelectedItem()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)

        //logout
        val emptyLoggedInUser = LoggedInUser("", "", "", "")
        loggedInUser=emptyLoggedInUser

        activity.startActivity(intent)
        activity.finishAffinity()
    }

    private fun isCurrentActivity(targetActivity: Class<*>): Boolean {
        return activity.javaClass == targetActivity
    }

    private fun highlightSelectedItem() {
        when {
            isCurrentActivity(AdminHomeActivity::class.java) -> bottomNavigationView.selectedItemId = R.id.nav_home
            isCurrentActivity(ProfileActivity::class.java) -> bottomNavigationView.selectedItemId = R.id.nav_profile
        }
    }
}
