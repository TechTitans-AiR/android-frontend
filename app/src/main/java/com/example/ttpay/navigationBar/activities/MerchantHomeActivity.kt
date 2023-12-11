package com.example.ttpay.navigationBar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsActivity
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem.AllCatalogsMerchantActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.transactions.activity_transactions.AllTransactionsActivity
import com.example.ttpay.transactions.activity_transactions.AllTransactionsMerchantActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MerchantHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_home)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
    }

    fun onAllTransactionsClick(view: View) {
        val intent = Intent(this, AllTransactionsMerchantActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onCatalogItemClick(view: View) {
        val intent = Intent(this, AllCatalogsMerchantActivity::class.java)
        startActivity(intent)
        finish()
    }
}