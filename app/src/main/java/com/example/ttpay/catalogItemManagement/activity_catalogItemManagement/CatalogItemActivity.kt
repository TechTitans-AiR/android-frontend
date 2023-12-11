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
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.CatalogAdapter
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogItemActivity : AppCompatActivity() {


    private lateinit var userId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_item)

        AndroidThreeTen.init(this)

        // Retrieve user ID from the intent
        userId = intent.getStringExtra("userId") ?: ""
        Log.d("CatalogItemActivity", "UserID: $userId")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_catalogs)
        progressBar = findViewById(R.id.loadingProgressBar)

        // Set up RecyclerView
        adapter = CatalogAdapter(emptyList()) { catalog ->
            // Handle catalog item click, if needed
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Fetch and display user catalogs
        fetchUserCatalogs()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllCatalogsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserCatalogs() {
        Log.d("CatalogItemActivity", "fetchUserCatalogs() started")
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getUserCatalogs(userId)

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                Log.d("CatalogItemActivity", "onResponse() called")
                hideLoading()
                if (response.isSuccessful) {
                    val catalog = response.body()
                    if (catalog != null) {
                        adapter.updateData(listOf(catalog))
                        Log.d("CatalogItemActivity", "Response: $response")
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Catalog>, t: Throwable) {
                Log.e("CatalogItemActivity", "onFailure() called", t)
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
            .setMessage("Error fetching catalogs.")
            .setPositiveButton("Retry") { _, _ ->
                fetchUserCatalogs()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}