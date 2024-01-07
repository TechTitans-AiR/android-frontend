package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class DetailsServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    //UI elements to display service details
    private lateinit var serviceNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var serviceProviderTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var currencyTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var durationUnitTextView: TextView
    private lateinit var availabilityTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_service)

        val B_back : ImageView = findViewById(R.id.back_ac)

        // Initialization of UI elements
        serviceNameTextView = findViewById(R.id.serviceNameTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        serviceProviderTextView = findViewById(R.id.serviceProviderTextView)
        priceTextView = findViewById(R.id.priceTextView)
        currencyTextView = findViewById(R.id.currencyTextView)
        durationTextView = findViewById(R.id.durationTextView)
        durationUnitTextView = findViewById(R.id.durationUnitTextView)
        availabilityTextView = findViewById(R.id.availabilityTextView)
        locationTextView = findViewById(R.id.locationTextView)
        progressBar = findViewById(R.id.progressBar)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        B_back.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}