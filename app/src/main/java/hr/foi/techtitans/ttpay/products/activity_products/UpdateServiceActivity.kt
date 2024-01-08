package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var serviceId: String

    // UI elements
    private lateinit var btnBack: ImageView
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
        btnBack = findViewById(R.id.imgView_back)
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("UpdateServiceActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }

        // Get service details based on the provided serviceId
        serviceId = intent.getStringExtra("serviceId") ?: ""
        if (serviceId != null) {
            getServiceDetails(serviceId)
        }
    }

    private fun updateUIWithServiceDetails(service: Service?) {
        // Update the UI elements with the fetched service details
        if (service != null) {
            serviceNameEditText.setText(service.serviceName)
            descriptionEditText.setText(service.description)
            serviceProviderEditText.setText(service.serviceProvider)
            priceEditText.setText(service.price.toString())
            currencyEditText.setText(service.currency)
            durationEditText.setText(service.duration.toString())
            durationUnitEditText.setText(service.durationUnit)
            availabilityEditText.setText(service.availability)
            locationEditText.setText(service.serviceLocation)

        } else {
            serviceNameEditText.text.clear()
            descriptionEditText.text.clear()
            priceEditText.text.clear()
            currencyEditText.text.clear()
            serviceProviderEditText.text.clear()
            durationEditText.text.clear()
            durationUnitEditText.text.clear()
            availabilityEditText.text.clear()
            locationEditText.text.clear()
            Toast.makeText(this@UpdateServiceActivity, "Service details not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getServiceDetails(serviceId: String) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServiceDetails(serviceId)
        call.enqueue(object : Callback<Service> {
            override fun onResponse(call: Call<Service>, response: Response<Service>) {
                if (response.isSuccessful) {
                    val service = response.body()
                    updateUIWithServiceDetails(service)
                } else {
                    Toast.makeText(this@UpdateServiceActivity, "Failed to retrieve article details", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<Service>, t: Throwable) {
                Log.e("UpdateArticleActivity", "Error: ${t.message}", t)
                Toast.makeText(this@UpdateServiceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}