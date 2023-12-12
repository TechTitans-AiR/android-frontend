package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsActivity
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateCatalogItemActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_catalog_item)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllCatalogsActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        // Button to start creating the catalog
        val btnStartCreatingCatalog: Button = findViewById(R.id.btn_start_creating_catalog)
        btnStartCreatingCatalog.setOnClickListener {
            val intent = Intent(this, SelectArticlesActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }
}