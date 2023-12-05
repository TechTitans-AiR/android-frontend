package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateNewProductActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_product)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

    }
}