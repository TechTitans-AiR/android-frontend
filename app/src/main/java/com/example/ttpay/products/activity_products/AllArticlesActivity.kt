package com.example.ttpay.products.activity_products

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.model.Article
import com.example.ttpay.model.ArticleAdapter
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.products.network_catalogItemManagement.ServiceCatalogItemManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllArticlesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private val adapter = ArticleAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_articles)

        recyclerView = findViewById(R.id.recyclerView_all_articles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set adapter on recyclerView
        recyclerView.adapter = adapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchArticles()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AllProductsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusIconArticlesClick(view: View) {
        val intent = Intent(this, CreateNewProductActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Fetching all articles
    private fun fetchArticles() {
        showLoading()
        val service = RetrofitClient.instance.create(ServiceCatalogItemManagement::class.java)
        val call = service.getArticles()

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    Log.d("AllArticlesActivity", "Articles fetched successfully: $articles")
                    adapter.updateData(articles)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchArticles()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}