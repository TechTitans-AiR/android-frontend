package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.R
import com.example.ttpay.model.User
import com.example.ttpay.model.UserAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem.CreateCatalogItemActivity
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.CatalogAdapter
import com.example.ttpay.model.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCatalogsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private val catalogAdapter = CatalogAdapter(emptyList()) { catalog ->
        openDetailedCatalogItemActivity(catalog.id)
    }
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_catalogs)

        userUsername = intent.getStringExtra("username") ?: ""

        recyclerView = findViewById(R.id.recyclerView_all_catalogs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = catalogAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchCatalogs()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusCatalogIconClick(view: View) {
        val intent = Intent(this, CreateCatalogItemActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    private fun openDetailedCatalogItemActivity(catalogId: String?) {
        val intent = Intent(this, DetailedCatalogItemActivity::class.java)
        intent.putExtra("catalogId", catalogId)
        intent.putExtra("username", userUsername)
        startActivity(intent)
    }

    private fun fetchCatalogs() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogs()

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                Log.d("AllCatalogsActivity", "Response code: ${response.code()}")
                hideLoading()
                if (response.isSuccessful) {
                    val catalogs = response.body() ?: emptyList()
                    Log.d("AllCatalogsActivity", "Catalogs fetched successfully: $catalogs")
                    catalogAdapter.updateData(catalogs)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Catalog>>, t: Throwable) {
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
                fetchCatalogs()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

}