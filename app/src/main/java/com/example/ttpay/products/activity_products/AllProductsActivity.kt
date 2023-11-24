package com.example.ttpay.products.activity_products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllProductsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

    }

    // Handle click on "All Articles" CardView
    fun onAllArticlesClick(view: View) {
        val intent = Intent(this, AllArticlesActivity::class.java)
        startActivity(intent)
    }

    // Handle click on "All Services" CardView
    fun onAllServicesClick(view: View) {
        val intent = Intent(this, AllServicesActivity::class.java)
        startActivity(intent)
    }
}