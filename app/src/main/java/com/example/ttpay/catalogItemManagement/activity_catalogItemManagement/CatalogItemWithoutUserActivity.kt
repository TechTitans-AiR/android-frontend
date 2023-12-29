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
import com.example.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import com.example.ttpay.catalogItemManagement.model_catalogItemManagement.CatalogAdapter
import com.example.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.example.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogItemWithoutUserActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_item_without_user)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_all_catalogs)
        progressBar = findViewById(R.id.loadingProgressBar)

        adapter = CatalogAdapter(emptyList()) { catalog ->
            val intent = Intent(this, DetailedCatalogItemActivity::class.java)
            intent.putExtra("catalogId", catalog.id)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchCatalogsWithoutUsers()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllCatalogsActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCatalogsWithoutUsers() {
        Log.d("CatalogItemWithoutUserActivity", "fetchCatalogsWithoutUsers() started")
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogs()

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                Log.d("CatalogItemWithoutUserActivity", "onResponse() called")
                hideLoading()
                if (response.isSuccessful) {
                    val allCatalogs = response.body()
                    if (allCatalogs != null) {
                        // Filter catalogs that do not have users
                        val catalogsWithoutUsers = allCatalogs.filter { catalog ->
                            catalog.users == null || catalog.users.isEmpty()
                        }
                        adapter.updateData(catalogsWithoutUsers)
                        Log.d("CatalogItemWithoutUserActivity", "Response: $response")
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Catalog>>, t: Throwable) {
                Log.e("CatalogItemWithoutUserActivity", "onFailure() called", t)
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
                fetchCatalogsWithoutUsers()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}