package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

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
import com.example.ttpay.model.Service
import com.example.ttpay.model.ServiceAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProductsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var recyclerViewServices: RecyclerView
    private val articleAdapter = ArticleAdapter(emptyList())
    private val serviceAdapter = ServiceAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)

        recyclerViewArticles = findViewById(R.id.recyclerView_all_articles)
        recyclerViewServices = findViewById(R.id.recyclerView_all_services)

        recyclerViewArticles.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.layoutManager = LinearLayoutManager(this)

        recyclerViewArticles.adapter = articleAdapter
        recyclerViewServices.adapter = serviceAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchArticles()
        fetchServices()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusIconProductsClick(view: View) {
        // Implement logic for adding new products
        // You may want to navigate to a different activity for adding new products
    }

    private fun fetchArticles() {
        showLoading()
        val service = RetrofitClient.instance.create(ServiceCatalogItemManagement::class.java)
        val call = service.getArticles()

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Articles fetched successfully: $articles")
                    articleAdapter.updateData(articles)
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

    private fun fetchServices() {
        showLoading()
        val service = RetrofitClient.instance.create(ServiceCatalogItemManagement::class.java)
        val call = service.getServices()

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Services fetched successfully: $services")
                    serviceAdapter.updateData(services)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
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
                fetchServices()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}