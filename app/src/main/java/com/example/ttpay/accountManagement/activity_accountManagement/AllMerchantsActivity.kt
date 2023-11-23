package com.example.ttpay.accountManagement.activity_accountManagement

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
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.R
import com.example.ttpay.model.User
import com.example.ttpay.model.MerchantAdapter
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMerchantsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var recyclerView: RecyclerView
    private val adapter = MerchantAdapter(emptyList())
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_merchants)
        recyclerView = findViewById(R.id.recyclerView_all_merchants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set adapter on recyclerView
        recyclerView.adapter = adapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchMerchants()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Fetching all users
    private fun fetchMerchants() {
        showLoading()
        val service = RetrofitClient.instance.create(ServiceAccountManagement::class.java)
        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    Log.d("AllMerchantsActivity", "Users fetched successfully: $users")
                    adapter.updateData(users)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
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
                fetchMerchants()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}