package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class CreateServiceActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var loadingDataProgressBar: ProgressBar
    private lateinit var actionBack: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    // Declare and initialize EditText and Spinner
    private lateinit var editTextServiceName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextServiceProvider: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var editTextDuration: EditText
    private lateinit var spinnerDurationUnit: Spinner
    private lateinit var editTextAvailability: EditText
    private lateinit var editTextServiceLocation: EditText
    private lateinit var btnCreateNewService: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_service)

        loadingDataProgressBar = findViewById(R.id.loadingData)
        actionBack = findViewById(R.id.action_back)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Initialize EditText and Spinner
        editTextServiceName = findViewById(R.id.editTextServiceName)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextServiceProvider = findViewById(R.id.editTextServiceProvider)
        editTextPrice = findViewById(R.id.editTextPrice)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        editTextDuration = findViewById(R.id.editTextDuration)
        spinnerDurationUnit = findViewById(R.id.spinnerDurationUnit)
        editTextAvailability = findViewById(R.id.editTextAvailability)
        editTextServiceLocation = findViewById(R.id.editTextServiceLocation)
        btnCreateNewService = findViewById(R.id.btnCreateNewService)


        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        actionBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateServiceActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            setResult(RESULT_OK, intent)
            finish()
        }


    }
}