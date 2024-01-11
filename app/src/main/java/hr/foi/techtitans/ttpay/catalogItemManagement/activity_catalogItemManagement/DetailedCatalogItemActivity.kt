package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

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
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.UnifiedItemAdapter
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.utilities.DateFormatter
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
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var unifiedItemAdapterArticles: UnifiedItemAdapter<Article>
    private lateinit var unifiedItemAdapterServices: UnifiedItemAdapter<Service>
    private lateinit var textViewUsers: TextView
    private lateinit var textViewDateCreated: TextView
    private lateinit var textViewDateModified: TextView
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_catalog_item)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        progressBar = findViewById(R.id.loadingProgressBar)
        textViewCatalogName = findViewById(R.id.textView_catalogName)
        recyclerViewArticles = findViewById(R.id.recyclerViewArticles)
        recyclerViewServices = findViewById(R.id.recyclerViewServices)
        textViewUsers = findViewById(R.id.textView_users)
        textViewDateCreated = findViewById(R.id.textView_dateCreated)
        textViewDateModified = findViewById(R.id.textView_dateModified)

        catalogId = intent.getStringExtra("catalogId") ?: ""
        Log.d("CatalogItemWithoutUserActivity", "Catalog id: $catalogId")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        // Initialize RecyclerViews and Adapters
        initializeRecyclerView(recyclerViewArticles)
        initializeRecyclerView(recyclerViewServices)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            onBackPressed()
        }

        fetchCatalogDetails()
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun fetchCatalogDetails() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogDetails(loggedInUser.token, catalogId)

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                hideLoading()
                if (response.isSuccessful) {
                    val catalog = response.body()
                    if (catalog != null) {
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
        val articlesList = parseStringList(catalog.articles)
        if (articlesList.isNullOrEmpty()) {
            recyclerViewArticles.visibility = View.GONE
        } else {
            fetchAndSetArticles(articlesList)
        }

        // Set services
        val servicesList = parseStringList(catalog.services)
        if (servicesList.isNullOrEmpty()) {
            recyclerViewServices.visibility = View.GONE
        } else {
            fetchAndSetServices(servicesList)
        }

        // Fetch and set user names
        val usersList = parseStringList(catalog.users)
        if (usersList.isNullOrEmpty()) {
            textViewUsers.text = ""
        } else {
            fetchAndSetUserNames(usersList)
        }

        // Set date created and date modified
        textViewDateCreated.text = DateFormatter.formatDate(catalog.date_created)
        textViewDateModified.text = DateFormatter.formatDate(catalog.date_modified)
    }

    private fun fetchAndSetArticles(articleIds: List<String>?) {
        if (articleIds.isNullOrEmpty()) {
            recyclerViewArticles.visibility = View.GONE
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val articleDetailsList = mutableListOf<Article>()
        val remainingCount = AtomicInteger(articleIds.size)

        for (articleId in articleIds) {
            service.getArticleDetails(loggedInUser.token, articleId).enqueue(object : Callback<Article> {
                override fun onResponse(call: Call<Article>, response: Response<Article>) {
                    if (response.isSuccessful) {
                        val article = response.body()
                        article?.let {
                            articleDetailsList.add(it)
                        }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        showArticles(articleDetailsList)
                    }
                }

                override fun onFailure(call: Call<Article>, t: Throwable) {
                    remainingCount.decrementAndGet()
                    hideLoading()
                    showErrorDialog()
                }
            })
        }
    }

    private fun showArticles(articleList: List<Article>) {
        recyclerViewArticles.visibility = View.VISIBLE
        unifiedItemAdapterArticles = UnifiedItemAdapter(articleList, isService = false)
        recyclerViewArticles.adapter = unifiedItemAdapterArticles
    }

    private fun fetchAndSetServices(serviceIds: List<String>?) {
        if (serviceIds.isNullOrEmpty()) {
            recyclerViewServices.visibility = View.GONE
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val serviceDetailsList = mutableListOf<Service>()
        val remainingCount = AtomicInteger(serviceIds.size)

        for (serviceId in serviceIds) {
            service.getServiceDetails(loggedInUser.token, serviceId).enqueue(object : Callback<Service> {
                override fun onResponse(call: Call<Service>, response: Response<Service>) {
                    if (response.isSuccessful) {
                        val service = response.body()
                        service?.let {
                            serviceDetailsList.add(it)
                        }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        showServices(serviceDetailsList)
                    }
                }

                override fun onFailure(call: Call<Service>, t: Throwable) {
                    remainingCount.decrementAndGet()
                    hideLoading()
                    showErrorDialog()
                }
            })
        }
    }

    private fun showServices(serviceList: List<Service>) {
        recyclerViewServices.visibility = View.VISIBLE
        unifiedItemAdapterServices = UnifiedItemAdapter(serviceList, isService = true)
        recyclerViewServices.adapter = unifiedItemAdapterServices
    }

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
                    textViewUsers.text = "${userNames.joinToString(", ")}"
                }
            }
        }
    }

    private fun fetchUserDetails(userId: String, callback: (User) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(loggedInUser.token, userId)

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
        return jsonArrayString?.let {
            try {
                val jsonArray = JSONArray(it)
                List(jsonArray.length()) { index -> jsonArray.getString(index) }
            } catch (e: JSONException) {
                null
            }
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