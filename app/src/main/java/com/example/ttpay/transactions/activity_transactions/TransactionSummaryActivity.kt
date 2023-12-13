package com.example.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.TransactionSummaryAdapter
import com.example.ttpay.transactions.network_transactions.ShoppingCartItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransactionSummaryActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var shoppingCartItems: MutableList<ShoppingCartItem>
    private var totalAmount: Double = 0.0
    private lateinit var tv_total_amount_summary: TextView
    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_summary)

        userUsername = intent.getStringExtra("username") ?: ""
        shoppingCartItems =
            intent.getSerializableExtra("shoppingCartItems") as MutableList<ShoppingCartItem>
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        Log.d("TransactionSummaryActivity", "onCreate: username: $userUsername")
        Log.d("TransactionSummaryActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("TransactionSummaryActivity", "onCreate: totalAmount: $totalAmount")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        // set Recycler View for shoppingCartItems
        val recycler_transaction_details = findViewById<RecyclerView>(R.id.recycler_transaction_details)
        val layoutManager = LinearLayoutManager(this)
        recycler_transaction_details.layoutManager = layoutManager

        val adapter = TransactionSummaryAdapter(shoppingCartItems)
        recycler_transaction_details.adapter = adapter

        // set text for total amount
        tv_total_amount_summary = findViewById(R.id.tv_total_amount_summary)
        tv_total_amount_summary.text = "Total Amount: €${"%.2f".format(totalAmount)}"
    }
}