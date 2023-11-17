package com.example.ttpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.model.MerchantAdapter
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllMerchantsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_merchants)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        // Example of list merchants (later real data)
        val merchants = listOf("Merchant 1", "Merchant 2", "Merchant 3", "Merchant 4", "Merchant 5",
            "Merchant 6", "Merchant 7", "Merchant 1", "Merchant 2", "Merchant 3", "Merchant 4",
            "Merchant 5", "Merchant 6", "Merchant 7")

        // set RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_all_merchants)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MerchantAdapter(merchants)
        recyclerView.adapter = adapter

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}