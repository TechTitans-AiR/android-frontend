package com.example.ttpay.products.activity_products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateProductActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllProductsActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }
    }
}