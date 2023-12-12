package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.ttpay.R
import com.example.ttpay.model.Article
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectServicesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_services)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectArticlesActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        //get the list of the added articles
        val selectedArticles: ArrayList<Article>? = intent.getSerializableExtra("selected_articles") as? ArrayList<Article>
        //Print the list of articles in logcat
        selectedArticles?.let {
            for (article in it) {
                Log.d("SelectServicesActivity", "Article: $article")
            }
        }

        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }
}