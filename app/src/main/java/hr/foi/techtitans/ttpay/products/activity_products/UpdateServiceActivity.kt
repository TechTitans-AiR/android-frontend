package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class UpdateServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var serviceId: String

    // UI elements
    private lateinit var btnCancel: Button
    private lateinit var btnEditData: Button
    private lateinit var serviceNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var serviceProviderEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var currencyEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var durationUnitEditText: EditText
    private lateinit var availabilityEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_service)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        // Initialize UI elements
        btnCancel = findViewById(R.id.btnCancel)
        btnEditData = findViewById(R.id.btnEditData)
        serviceNameEditText = findViewById(R.id.serviceNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        serviceProviderEditText = findViewById(R.id.serviceProviderEditText)
        priceEditText = findViewById(R.id.priceEditText)
        currencyEditText = findViewById(R.id.currencyEditText)
        durationEditText = findViewById(R.id.durationEditText)
        durationUnitEditText = findViewById(R.id.durationUnitEditText)
        availabilityEditText = findViewById(R.id.availabilityEditText)
        locationEditText = findViewById(R.id.locationEditText)
        progressBar = findViewById(R.id.progressBar)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("UpdateServiceActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}