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
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
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

    // Original values that can be changed
    private lateinit var originalDescription: String
    private lateinit var originalDuration: String
    private lateinit var originalDurationUnit: String
    private lateinit var originalPrice: String
    private lateinit var originalCurrency: String
    private lateinit var originalServiceLocation: String

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_service)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        // Initialize UI elements
        btnBack = findViewById(R.id.back_ac)
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
            setResult(RESULT_OK, intent)
            finish()
        }

        // Get service details based on the provided serviceId
        serviceId = intent.getStringExtra("serviceId") ?: ""
        if (serviceId != null) {
            getServiceDetails(serviceId)
        }

        btnCancel.visibility = View.GONE
        btnCancel.setOnClickListener {
            revertChanges()
        }

        btnEditData.setOnClickListener {
            toggleEditMode()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        if (isEditMode) {
            enableEditMode()
        } else {
            saveChanges()
            disableEditMode()
        }
    }

    private fun enableEditMode() {
        // Enable editing for specified fields
        descriptionEditText.isEnabled = true
        priceEditText.isEnabled = true
        currencyEditText.isEnabled = true
        durationEditText.isEnabled = true
        durationUnitEditText.isEnabled = true
        locationEditText.isEnabled = true

        // Save original values for later comparison
        originalDescription = descriptionEditText.text.toString()
        originalPrice = priceEditText.text.toString()
        originalCurrency = currencyEditText.text.toString()
        originalDuration = durationEditText.text.toString()
        originalDurationUnit = durationUnitEditText.text.toString()
        originalServiceLocation = locationEditText.text.toString()

        btnCancel.visibility = View.VISIBLE
        btnEditData.text = "Save Changes"
    }

    private fun disableEditMode() {
        // Disable editing for specified fields
        descriptionEditText.isEnabled = false
        priceEditText.isEnabled = false
        currencyEditText.isEnabled = false
        durationEditText.isEnabled = false
        durationUnitEditText.isEnabled = false
        locationEditText.isEnabled = false

        btnCancel.visibility = View.GONE
        btnEditData.text = "Edit Data"
    }

    private fun isFieldChanged(editText: EditText, originalValue: String?): Boolean {
        val currentValue = editText.text.toString()
        return currentValue != originalValue
    }
    private fun collectUpdatedFields(): Map<String, Any> {
        val updatedFields = mutableMapOf<String, Any>()

        if (isFieldChanged(descriptionEditText, originalDescription)) {
            updatedFields["description"] = descriptionEditText.text.toString()
        }
        if (isFieldChanged(priceEditText, originalPrice)) {
            updatedFields["price"] = priceEditText.text.toString().toDouble()
        }
        if (isFieldChanged(currencyEditText, originalCurrency)) {
            updatedFields["currency"] = currencyEditText.text.toString()
        }
        if (isFieldChanged(durationEditText, originalDuration)) {
            updatedFields["duration"] = durationEditText.text.toString().toInt()
        }
        if (isFieldChanged(durationUnitEditText, originalDurationUnit)) {
            updatedFields["durationUnit"] = durationUnitEditText.text.toString()
        }
        if (isFieldChanged(locationEditText, originalServiceLocation)) {
            updatedFields["serviceLocation"] = locationEditText.text.toString()
        }
        return updatedFields
    }

    private fun updateServiceDetails(serviceId: String, updatedFields: Map<String, Any>, token: String) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.updateService(serviceId, updatedFields, "Bearer $token")
        Log.d("UpdateServiceActivity", "Service ID: $serviceId, Token: $token, Updated Fields: $updatedFields")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("UpdateServiceActivity", "Service updated successfully")
                    Toast.makeText(this@UpdateServiceActivity, "Service updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("UpdateServiceActivity", "Failed to update service. Response code: ${response.code()}")
                    Toast.makeText(this@UpdateServiceActivity, "Failed to update service", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("UpdateServiceActivity", "Update service failed: ${t.message}", t)
                Toast.makeText(this@UpdateServiceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveChanges() {
        // Log statement
        Log.d("UpdateServiceActivity", "Entered saveChanges")

        Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()

        // Collect and send only updated fields to the server
        val updatedFields = collectUpdatedFields()

        // Check if there are any changes before making the API call
        if (updatedFields.isNotEmpty()) {
            updateServiceDetails(serviceId, updatedFields, loggedInUser.token)
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
        val call = service.getServiceDetails(loggedInUser.token, serviceId)
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

    private fun revertChanges() {
        Log.d("UpdateServiceActivity", "Entered revertChanges")
        // Revert changes to original values
        descriptionEditText.setText(originalDescription)
        priceEditText.setText(originalPrice)
        currencyEditText.setText(originalCurrency)
        durationEditText.setText(originalDuration)
        durationUnitEditText.setText(originalDurationUnit)
        locationEditText.setText(originalServiceLocation)

        btnCancel.visibility = View.GONE
        btnEditData.text = "Edit Data"

        descriptionEditText.isEnabled = false
        priceEditText.isEnabled = false
        currencyEditText.isEnabled = false
        durationEditText.isEnabled = false
        durationUnitEditText.isEnabled = false
        locationEditText.isEnabled = false
    }
}