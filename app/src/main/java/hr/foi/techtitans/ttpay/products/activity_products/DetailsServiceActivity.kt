package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    //UI elements to display service details
    private lateinit var serviceNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var serviceProviderTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var durationTextView: TextView
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
        durationTextView = findViewById(R.id.durationTextView)
        availabilityTextView = findViewById(R.id.availabilityTextView)
        locationTextView = findViewById(R.id.locationTextView)
        progressBar = findViewById(R.id.progressBar)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        // Get service ID from intent
        val serviceId = intent.getStringExtra("serviceId")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        fetchServiceDetails(serviceId)

        B_back.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }

    private fun fetchServiceDetails(serviceId: String?) {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServiceDetails(loggedInUser.token, serviceId.orEmpty())
        call.enqueue(object : Callback<Service> {
            override fun onResponse(call: Call<Service>, response: Response<Service>) {
                hideLoading()
                if (response.isSuccessful) {
                    val service = response.body()
                    if (service != null) {
                        serviceNameTextView.text = service.serviceName
                        descriptionTextView.text = service.description
                        serviceProviderTextView.text = service.serviceProvider
                        priceTextView.text = "${service.price} ${service.currency}"
                        durationTextView.text = "${service.duration} ${service.durationUnit}"
                        availabilityTextView.text = service.availability
                        locationTextView.text = service.serviceLocation
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Service>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching service details.")
            .setPositiveButton("Retry") { _, _ ->
                val serviceId = intent.getStringExtra("serviceId")
                fetchServiceDetails(serviceId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}