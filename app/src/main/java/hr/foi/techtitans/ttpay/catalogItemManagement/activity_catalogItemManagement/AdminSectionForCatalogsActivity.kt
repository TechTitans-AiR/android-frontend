package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.transactions.activity_transactions.AllTransactionsActivity

class AdminSectionForCatalogsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_section_for_catalogs)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllTransationsActivity - LoggedInUser", loggedInUser.toString())
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }

    fun onCatalogsSectionClick(view: View) {
        val intent = Intent(this, AllCatalogsActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onAllMerchantsClick - LoggedInUser",loggedInUser.toString())
        intent.putExtra("username", userUsername)
        startActivity(intent)
    }

    fun onOnlyMyCatalogsSectionClick(view: View) {
        val intent = Intent(this, AllCatalogsAdminAsMerchantActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onAllMerchantsClick - LoggedInUser",loggedInUser.toString())
        intent.putExtra("username", userUsername)
        startActivity(intent)
    }
}