package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.NewCatalog
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.AddedArticleAdapter
import com.example.ttpay.model.Article
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.SelectArticleAdapter
import com.example.ttpay.model.SelectServiceAdapter
import com.example.ttpay.model.Service
import com.example.ttpay.model.ServiceAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class SelectServicesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var progressBar: ProgressBar
    //for selecting service
    private lateinit var recyclerViewSelectServices: RecyclerView
    private lateinit var selectServiceAdapter: SelectServiceAdapter

    //which service is selected
    private lateinit var addedServiceAdapter: SelectServiceAdapter
    private lateinit var recyclerViewAddedServices: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_services)

        //nav
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        //btn back
        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectArticlesActivity::class.java)
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

        //get all services
        fetchServices();



        //continue to select user
        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }
    private fun fetchServices() {
        val recyclerView=R.id.recyclerView_select_services

        val retrofit=RetrofitClient.getInstance(8081)
        val service=retrofit.create(ServiceProducts::class.java)

        //call for method new catalog
        val call=service.getServices()

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val listServices = response.body() ?: emptyList()
                    selectServiceAdapter.updateData(listServices)
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
    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchServices()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}