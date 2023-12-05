package com.example.ttpay.navigationBar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ttpay.R
import com.example.ttpay.accountManagement.activity_accountManagement.AllMerchantsActivity
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.products.activity_products.AllProductsActivity
import com.example.ttpay.transactions.activity_transactions.AllTransactionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
    }

    fun onAllMerchantsClick(view: View) {
        val intent = Intent(this, AllMerchantsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onAllTransactionsClick(view: View) {
        val intent = Intent(this, AllTransactionsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onAllProductsClick(view: View) {
        val intent = Intent(this, AllProductsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onCatalogItemClick(view: View) {
        val intent = Intent(this, AllCatalogsActivity::class.java)
        startActivity(intent)
        finish()
    }
}