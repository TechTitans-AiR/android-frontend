package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.NewService
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        btnCreateNewService.setOnClickListener {
            // Fetch data from EditText and Spinner
            val serviceName = editTextServiceName.text.toString()
            val description = editTextDescription.text.toString()
            val serviceProvider = editTextServiceProvider.text.toString()
            val price = editTextPrice.text.toString().toInt()
            val currency = spinnerCurrency.selectedItem.toString()
            val duration = editTextDuration.text.toString().toInt()
            val durationUnit = spinnerDurationUnit.selectedItem.toString()
            val availability = editTextAvailability.text.toString()
            val serviceLocation = editTextServiceLocation.text.toString()

            // Get the JWT token from the loggedInUser
            val jwtToken = loggedInUser.token

            // Create a NewService object
            val newService = NewService(
                serviceName,
                description,
                serviceProvider,
                price,
                currency,
                duration,
                availability,
                serviceLocation,
                durationUnit
            )

            // Call the createService endpoint
            createService(newService, jwtToken)

        }
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CNY", "INR") // Add more currencies as needed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
    }

    private fun setupDurationUnitSpinner() {
        val durationUnits = arrayOf("Seconds", "Minutes", "Hours", "Days", "Weeks", "Months", "Years")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDurationUnit.adapter = adapter
    }

    private fun createService(newService: NewService, jwtToken: String) {
        showLoading()

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.createService(newService, "Bearer $jwtToken")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                hideLoading()
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Service created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Error creating service. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, "Error creating service. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading() {
        loadingDataProgressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingDataProgressBar?.visibility = View.GONE
    }


}