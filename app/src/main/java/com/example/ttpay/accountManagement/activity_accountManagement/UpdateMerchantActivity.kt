package com.example.ttpay.accountManagement.activity_accountManagement


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.model.UserRole
import com.example.ttpay.model.UserStatus
import com.google.android.material.bottomnavigation.BottomNavigationView


class UpdateMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var userID:String
    private lateinit var user:User

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

        //for userID
        userID=intent.getStringExtra("userId") ?: ""
        Log.d("UpdateMerchantActivity userID:",userID)

        //initializing fields for data
        editTxtFirstName=findViewById(R.id.editText_first_name)
        editTxtLastName=findViewById(R.id.editText_last_name)
        editTxtDateOfBirth=findViewById(R.id.editText_date_of_birth)
        editTxtAddress=findViewById(R.id.editText_address)
        editTxtPhone=findViewById(R.id.editText_phone)
        editTxtEmail=findViewById(R.id.editText_email)
        editTxtUsername=findViewById(R.id.editText_username)
        editTxtPassword=findViewById(R.id.editText_password)
        spinnerStatus=findViewById(R.id.spinner_user_status)


        //Spinner
        val userRoleSpinner: Spinner = findViewById(R.id.spinner_user_role)
        val userRoleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(UserRole.admin.name, UserRole.merchant.name)
        )
        userRoleSpinner.adapter = userRoleAdapter

        // Postavljanje ArrayAdapter-a za UserStatus Spinner
        val userStatusSpinner: Spinner = findViewById(R.id.spinner_user_status)
        val userStatusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(UserStatus.active.name, UserStatus.inactive.name, UserStatus.blocked.name)
        )
        userStatusSpinner.adapter = userStatusAdapter
    }
}
