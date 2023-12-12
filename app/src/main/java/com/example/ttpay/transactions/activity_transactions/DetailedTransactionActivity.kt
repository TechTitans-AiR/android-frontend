package com.example.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailedTransactionActivity : AppCompatActivity() {

    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_transaction)

        transactionId = intent.getStringExtra("catalogId") ?: ""
        Log.d("DetailedTransactionActivity", "Transaction id: $transactionId")

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