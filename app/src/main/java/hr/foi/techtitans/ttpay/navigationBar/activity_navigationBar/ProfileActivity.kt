package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    private lateinit var txtViewFirstName: TextView
    private lateinit var txtViewLastName: TextView
    private lateinit var txtViewUserRole: TextView
    private lateinit var txtViewDateOfBirth: TextView
    private lateinit var txtViewDateCreated: TextView
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPassword: TextView
    private lateinit var txtViewEmail: TextView
    private lateinit var txtViewPhone: TextView
    private lateinit var txtViewAddress: TextView
    private lateinit var txtViewStatus: TextView

    private lateinit var btnEditData: Button
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtViewFirstName = findViewById(R.id.textView_FirstName)
        txtViewLastName = findViewById(R.id.textView_LastName)
        txtViewUserRole = findViewById(R.id.textView_userRole)
        txtViewDateOfBirth = findViewById(R.id.textView_dateOfBirth)
        txtViewDateCreated = findViewById(R.id.textView_dateCreated)
        txtViewUsername = findViewById(R.id.textView_username)
        txtViewPassword = findViewById(R.id.textView_password)
        txtViewEmail = findViewById(R.id.textView_email)
        txtViewPhone = findViewById(R.id.textView_phone)
        txtViewAddress = findViewById(R.id.textView_address)
        txtViewStatus = findViewById(R.id.textView_status)

        userUsername = intent.getStringExtra("username") ?: ""
        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        getUserDetails(loggedInUser.userId)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        btnEditData = findViewById(R.id.btnEditData)
        btnEditData.setOnClickListener {
            toggleEditMode()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            //mode for editing data
        } else {
            //mode for see updated info
        }
    }

    private fun getUserDetails(userId: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUserDetails(userId)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    // Set user data into text views
                    updateUserDetails(user)
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to retrieve user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserDetails(user: User?) {
        if (user != null) {
            // Update text views with user data
            txtViewFirstName.text = user.first_name
            txtViewLastName.text = user.last_name
            txtViewUserRole.text = user.userRole?.name ?: ""
            txtViewDateOfBirth.text = "Date of Birth: ${user.date_of_birth ?: ""}"
            txtViewDateCreated.text = "Date Created: ${user.date_created ?: ""}"
            txtViewUsername.text = "Username: ${user.username}"
            txtViewPassword.text = "Password: ${user.password}"
            txtViewEmail.text = "Email: ${user.email}"
            txtViewPhone.text = "Phone: ${user.phone ?: ""}"
            txtViewAddress.text = "Address: ${user.address ?: ""}"
            txtViewStatus.text = "Status: ${user.userStatus?.name ?: ""}"
        } else {
            // If user is null, set values in text views to empty string and display the toast message
            txtViewFirstName.text = ""
            txtViewLastName.text = ""
            txtViewUserRole.text = ""
            txtViewDateOfBirth.text = ""
            txtViewDateCreated.text = ""
            txtViewUsername.text = ""
            txtViewPassword.text = ""
            txtViewEmail.text = ""
            txtViewPhone.text = ""
            txtViewAddress.text = ""
            txtViewStatus.text = ""
            Toast.makeText(this@ProfileActivity, "User details not available", Toast.LENGTH_SHORT).show()
        }
    }
}