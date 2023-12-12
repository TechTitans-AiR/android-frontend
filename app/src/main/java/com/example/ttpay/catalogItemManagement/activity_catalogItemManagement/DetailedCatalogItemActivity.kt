package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Article
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.Service
import com.example.ttpay.model.User
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger

class DetailedCatalogItemActivity : AppCompatActivity() {
    private lateinit var catalogId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewCatalogName: TextView
    private lateinit var textViewArticles: TextView
    private lateinit var textViewServices: TextView
    private lateinit var textViewUsers: TextView
    private lateinit var textViewDateCreated: TextView
    private lateinit var textViewDateModified: TextView
    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_catalog_item)

        // Initialize views
        progressBar = findViewById(R.id.loadingProgressBar)
        textViewCatalogName = findViewById(R.id.textView_catalogName)
        textViewArticles = findViewById(R.id.textView_articles)
        textViewServices = findViewById(R.id.textView_services)
        textViewUsers = findViewById(R.id.textView_users)
        textViewDateCreated = findViewById(R.id.textView_dateCreated)
        textViewDateModified = findViewById(R.id.textView_dateModified)

        // Retrieve catalogId from the intent
        catalogId = intent.getStringExtra("catalogId") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        // Call the function to fetch and display catalog details
        fetchCatalogDetails()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCatalogDetails() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogDetails(catalogId)

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                hideLoading()
                if (response.isSuccessful) {
                    val catalog = response.body()
                    if (catalog != null) {
                        // Update the UI with catalog details
                        updateUIWithCatalogDetails(catalog)
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Catalog>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun updateUIWithCatalogDetails(catalog: Catalog) {
        // Set catalog name
        textViewCatalogName.text = catalog.name

        // Set articles
        fetchAndSetArticles(parseStringList(catalog.articles))

        // Set services
        fetchAndSetServices(parseStringList(catalog.services))

        // Fetch and set user names
        fetchAndSetUserNames(parseStringList(catalog.users))

        // Set date created and date modified
        textViewDateCreated.text = "Date Created: ${catalog.date_created}"
        textViewDateModified.text = "Date Modified: ${catalog.date_modified}"
    }

    private fun fetchAndSetArticles(articleIds: List<String>?) {
        if (articleIds.isNullOrEmpty()) {
            textViewArticles.text = "Articles: "
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val articleNames = mutableListOf<String>()
        val remainingCount = AtomicInteger(articleIds.size)

        for (articleId in articleIds) {
            service.getArticles().enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                    if (response.isSuccessful) {
                        val articles = response.body()
                        val article = articles?.find { it.id == articleId }
                        article?.let { articleNames.add(it.name) }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        textViewArticles.text = "Articles: ${articleNames.joinToString(", ")}"
                    }
                }

                override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                    remainingCount.decrementAndGet()
                    hideLoading()
                    showErrorDialog()
                }
            })
        }
    }

    private fun fetchAndSetServices(serviceIds: List<String>?) {
        if (serviceIds.isNullOrEmpty()) {
            textViewServices.text = "Services: "
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val serviceNames = mutableListOf<String>()
        val remainingCount = AtomicInteger(serviceIds.size)

        for (serviceId in serviceIds) {
            service.getServices().enqueue(object : Callback<List<Service>> {
                override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                    if (response.isSuccessful) {
                        val services = response.body()
                        val service = services?.find { it.id == serviceId }
                        service?.let { serviceNames.add(it.serviceName) }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        textViewServices.text = "Services: ${serviceNames.joinToString(", ")}"
                    }
                }

                override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                    remainingCount.decrementAndGet()
                    hideLoading()
                    showErrorDialog()
                }
            })
        }
    }

    // Function to fetch user names and update the UI
    private fun fetchAndSetUserNames(userIds: List<String>?) {
        if (userIds.isNullOrEmpty()) {
            textViewUsers.text = "Users: "
            return
        }

        val userNames = mutableListOf<String>()
        val remainingCount = AtomicInteger(userIds.size)

        for (userId in userIds) {
            fetchUserDetails(userId) { user ->
                userNames.add("${user.first_name} ${user.last_name}")
                if (remainingCount.decrementAndGet() == 0) {
                    // All user names fetched, update the UI
                    textViewUsers.text = "Users: ${userNames.joinToString(", ")}"
                }
            }
        }
    }

    // Function to fetch detailed information for a user
    private fun fetchUserDetails(userId: String, callback: (User) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        callback.invoke(user)
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    // Function to parse a JSON array represented as a string into a list of strings
    private fun parseStringList(jsonArrayString: String?): List<String>? {
        return try {
            val jsonArray = JSONArray(jsonArrayString)
            List(jsonArray.length()) { jsonArray.getString(it) }
        } catch (e: JSONException) {
            null
        }
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
                fetchCatalogDetails()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}