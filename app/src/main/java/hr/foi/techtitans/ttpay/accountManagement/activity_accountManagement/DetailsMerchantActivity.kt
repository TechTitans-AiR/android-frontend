package hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class DetailsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userId: String

    private lateinit var txtViewFullName: TextView
    private lateinit var txtViewUserRole: TextView
    private lateinit var txtViewDateOfBirth: TextView
    private lateinit var txtViewDateCreated: TextView
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPassword: TextView
    private lateinit var txtViewEmail: TextView
    private lateinit var txtViewPhone: TextView
    private lateinit var txtViewAddress: TextView
    private lateinit var txtViewStatus: TextView
    private lateinit var txtViewPin: TextView

    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_merchant)
        // Initialize views
        txtViewFullName = findViewById(R.id.textView_fullName)
        txtViewUserRole = findViewById(R.id.textView_userRole)
        txtViewDateOfBirth = findViewById(R.id.textView_dateOfBirth)
        txtViewDateCreated = findViewById(R.id.textView_dateCreated)
        txtViewUsername = findViewById(R.id.textView_username)
        txtViewPassword = findViewById(R.id.textView_password)
        txtViewEmail = findViewById(R.id.textView_email)
        txtViewPhone = findViewById(R.id.textView_phone)
        txtViewAddress = findViewById(R.id.textView_address)
        txtViewStatus = findViewById(R.id.textView_status)
        txtViewPin = findViewById(R.id.textView_pin)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        // Get user ID from intent
        userId = intent.getStringExtra("userId") ?: ""



        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        // Fetch and display merchant details
        fetchMerchantDetails()

        // Back button click listener
        val btnBack: ImageView = findViewById(R.id.imgView_back)
        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser",loggedInUser)
            finish()
        }
    }

    private fun fetchMerchantDetails() {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(loggedInUser.token, userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        displayMerchantDetails(it)
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("NetworkError", "Error: ${t.message}")
                Toast.makeText(this@DetailsMerchantActivity, "Network error occured!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayMerchantDetails(user: User) {
        txtViewFullName.text = "${user.first_name} ${user.last_name}"
        txtViewUserRole.text = user.userRole?.name ?: ""
        txtViewDateOfBirth.text = "Date of Birth: ${user.date_of_birth ?: ""}"
        txtViewDateCreated.text = "Date Created: ${user.date_created ?: ""}"
        txtViewUsername.text = "Username: ${user.username}"
        txtViewPassword.text = "${user.password.substring(0, 6) ?: ""}"
        txtViewEmail.text = "Email: ${user.email}"
        txtViewPhone.text = "Phone: ${user.phone ?: ""}"
        txtViewAddress.text = "Address: ${user.address ?: ""}"
        txtViewStatus.text = "Status: ${user.userStatus?.name ?: ""}"
        txtViewPin.text = "${user.pin?.substring(0, 6) ?: ""}"
    }
}