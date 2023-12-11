package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem.CreateCatalogItemActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.navigationBar.activities.MerchantHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllTransactionsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar

    /**
     * add adapter
     * private val adapter = UserAdapter(emptyList()) { user ->
            openCatalogItemActivity(user.id)
    }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions_merchant)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, MerchantHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusTransactionIconClick(view: View) {
        val intent = Intent(this, CreateTransactionActivity::class.java)
        startActivity(intent)
    }
}