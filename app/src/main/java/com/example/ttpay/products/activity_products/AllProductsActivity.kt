package com.example.ttpay.products.activity_products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
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

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Handle click on "All Articles" CardView
    fun onAllArticlesClick(view: View) {
        Log.d("AllProductsActivity", "onAllArticlesClick")
        val intent = Intent(this, AllArticlesActivity::class.java)
        startActivity(intent)
    }

    // Handle click on "All Articles" CardView
    fun onAllServicesClick(view: View) {
        Log.d("AllProductsActivity", "onAllServicesClick")
        val intent = Intent(this, AllServicesActivity::class.java)
        startActivity(intent)
    }


}