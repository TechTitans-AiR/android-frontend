package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllCatalogsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_catalogs)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val btnBack: ImageView = findViewById(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}