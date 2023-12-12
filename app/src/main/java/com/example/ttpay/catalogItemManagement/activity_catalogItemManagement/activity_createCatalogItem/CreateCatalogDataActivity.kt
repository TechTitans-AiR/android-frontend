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

class CreateCatalogDataActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var createButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var userUsername: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_catalog_data)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        createButton = findViewById(R.id.btn_create_catalog)
        createButton.setOnClickListener {
            //implement for create catalog item
        }
    }
}