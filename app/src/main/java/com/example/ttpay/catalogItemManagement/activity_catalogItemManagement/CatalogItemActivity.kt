package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.CatalogAdapter
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogItemActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewUserName: TextView
    private lateinit var userUsername: String

    private lateinit var catalogId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_item)

        userUsername = intent.getStringExtra("username") ?: ""

        userId = intent.getStringExtra("userId") ?: ""
        Log.d("CatalogItemActivity", "UserID: $userId")

        catalogId = intent.getStringExtra("catalogId") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_catalogs)
        progressBar = findViewById(R.id.loadingProgressBar)


        // Set up RecyclerView
        adapter = CatalogAdapter(emptyList()) {
            // Handle catalog item click

            adapter = CatalogAdapter(emptyList()) { catalog ->
                val intent = Intent(this, DetailedCatalogItemActivity::class.java)
                intent.putExtra("catalogId", catalog.id)
                intent.putExtra("username", userUsername)
                startActivity(intent)
            }

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            fetchUserCatalogs()

            textViewUserName = findViewById(R.id.textViewUserName)
            fetchUserDetails(userId)

            val imgBack: ImageView = findViewById(R.id.back_button)
            imgBack.setOnClickListener {
                val intent = Intent(this, AllCatalogsActivity::class.java)
                intent.putExtra("username", userUsername)
                startActivity(intent)
                finish()
            }

        }


    }
    private fun fetchUserCatalogs() {
        Log.d("CatalogItemActivity", "fetchUserCatalogs() started")
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getUserCatalogs(userId)

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(
                call: Call<List<Catalog>>,
                response: Response<List<Catalog>>
            ) {
                Log.d("CatalogItemActivity", "onResponse() called")
                hideLoading()
                Log.d("Response: ", response.toString())
                if (response.isSuccessful) {
                    val catalogs = response.body()
                    if (catalogs != null) {
                        adapter.updateData(catalogs)
                        Log.d("CatalogItemActivity", "Response: $response")
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Catalog>>, t: Throwable) {
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

        private fun fetchUserDetails(userId: String) {
            val retrofit = RetrofitClient.getInstance(8080)
            val service = retrofit.create(ServiceAccountManagement::class.java)
            val call = service.getUserDetails(userId)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            textViewUserName.text = "${user.first_name} ${user.last_name}"
                        }
                    } else {
                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("CatalogItemActivity", "onFailure() called", t)
                    hideLoading()
                    showErrorDialog()
                }
            })
        }
}

