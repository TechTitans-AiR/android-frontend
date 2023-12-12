package com.example.ttpay.navigationBar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsMerchantActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.transactions.activity_transactions.AllTransactionsMerchantActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MerchantHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_home)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        userUsername = intent.getStringExtra("username") ?: ""

        Log.d("MerchantHomeActivity", "User username: $userUsername")
    }

    fun onAllTransactionsClick(view: View) {
        val intent = Intent(this, AllTransactionsMerchantActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    fun onCatalogItemClick(view: View) {
        val intent = Intent(this, AllCatalogsMerchantActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }
}