package com.example.ttpay.products.activity_products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ttpay.R

class DetailsServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_service)

        val B_back : ImageView = findViewById(R.id.back_ac)

        userUsername = intent.getStringExtra("username") ?: ""

        B_back.setOnClickListener {
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}