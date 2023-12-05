package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectUserActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectServicesActivity::class.java)
            startActivity(intent)
            finish()
        }

        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            val intent = Intent(this, CreateCatalogDataActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}