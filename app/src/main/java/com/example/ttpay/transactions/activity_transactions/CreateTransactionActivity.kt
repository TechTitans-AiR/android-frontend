package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsMerchantActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.navigationBar.activities.MerchantHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CreateTransactionActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    private lateinit var userId: String
    private lateinit var userRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_transaction)

        //Dodajte kod za dohvat podataka iz intenta
        userId = intent.getStringExtra("userId") ?: ""
        userRole = intent.getStringExtra("userRole") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}