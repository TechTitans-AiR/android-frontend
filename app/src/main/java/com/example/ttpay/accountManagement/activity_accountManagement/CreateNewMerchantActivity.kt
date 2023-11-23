package com.example.ttpay.accountManagement.activity_accountManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.UserRole
import com.example.ttpay.model.UserStatus
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateNewMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_merchant)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

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