package com.example.ttpay.accountManagement.activity_accountManagement


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.model.UserRole
import com.example.ttpay.model.UserStatus
import com.example.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var userID: String
    private lateinit var user: User

    //prepare fields for write data
    private lateinit var editTxtFirstName: EditText
    private lateinit var editTxtLastName: EditText
    private lateinit var editTxtDateOfBirth: EditText
    private lateinit var editTxtAddress: EditText
    private lateinit var editTxtPhone: EditText
    private lateinit var editTxtEmail: EditText
    private lateinit var editTxtUsername: EditText
    private lateinit var editTxtPassword: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerRole: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_merchant)

        userUsername = intent.getStringExtra("username") ?: ""

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            intent.putExtra("username", userUsername)
            finish()
        }

        //initializing fields for data
        editTxtFirstName = findViewById(R.id.editText_first_name)
        editTxtLastName = findViewById(R.id.editText_last_name)
        editTxtDateOfBirth = findViewById(R.id.editText_date_of_birth)
        editTxtAddress = findViewById(R.id.editText_address)
        editTxtPhone = findViewById(R.id.editText_phone)
        editTxtEmail = findViewById(R.id.editText_email)
        editTxtUsername = findViewById(R.id.editText_username)
        editTxtPassword = findViewById(R.id.editText_password)
        spinnerStatus = findViewById(R.id.spinner_user_status)
        spinnerRole = findViewById(R.id.spinner_user_role)


        //for userID
        userID = intent.getStringExtra("userId") ?: ""
        Log.d("UpdateMerchantActivity userID:", userID)

        //call endpoint for user details
        Log.d("FetchMerchant: ", userID)
        fetchMerchants(userID) { user ->
            if (user != null) {
                editTxtFirstName.setText(user.first_name)
                editTxtLastName.setText(user.last_name)
                editTxtDateOfBirth.setText(user.date_of_birth)
                editTxtAddress.setText(user.address)
                editTxtPhone.setText(user.phone)
                editTxtEmail.setText(user.email)
                editTxtUsername.setText(user.username)
                editTxtPassword.setText(user.password)

                //Update the Spinner for the user's role
                val userRoleList = arrayOf(
                    UserRole.merchant,
                    UserRole.admin
                )

                // Adapter initialization for user role spinner
                val userRoleAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    userRoleList.map { it.name } // Use enum names for display
                )
                spinnerRole.adapter = userRoleAdapter
                Log.d("User Role: ", user.userRole?.id.toString())


                // Update the Spinner with the user's status
                val userStatusList = arrayOf(
                    UserStatus.active,
                    UserStatus.inactive,
                    UserStatus.blocked
                )

                // Adapter initialization for user status spinner
                val userStatusAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    userStatusList.map { it.name } // Use enum names for display
                )
                spinnerStatus.adapter = userStatusAdapter
                Log.d("User status: ", user.userStatus?.id.toString())
            } else {
                Toast.makeText(
                    this@UpdateMerchantActivity,
                    "User details are null.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("fetchMerchants(userId)", "User details are null!")
            }
        }
    }
}
private fun fetchMerchants(userId:String, callback: (User?) -> Unit) {
    val retrofit = RetrofitClient.getInstance(8080)//za account_management
    val service = retrofit.create(ServiceAccountManagement::class.java)
    val call = service.getUserDetails(userId)

    call.enqueue(object : Callback<User> {
        override fun onResponse(call: Call<User>, response: Response<User>) {
            val user: User? = if (response.isSuccessful) {
                response.body()
            } else {
                Log.d("Response message is: ", response.message())
                null
            }
            callback(user)
        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            Log.d("Failed getting user information: ", t.message.toString())
            callback(null)
        }
    })
}