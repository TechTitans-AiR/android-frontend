package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

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
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog.SelectArticlesActivity
import hr.foi.techtitans.ttpay.core.LoggedInUser
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
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var btn_edit:Button
    private  var catalog:Catalog? =Catalog(null, "", "", "", "", null, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_catalog_item)

        btn_edit=findViewById(R.id.btn_editCatalog)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        catalog=intent.getParcelableExtra("selectedCatalog")
        Log.d("Selected catalog: ", catalog.toString())

        progressBar = findViewById(R.id.loadingProgressBar)
        textViewCatalogName = findViewById(R.id.textView_catalogName)
        textViewArticles = findViewById(R.id.textView_articles)
        textViewServices = findViewById(R.id.textView_services)
        textViewUsers = findViewById(R.id.textView_users)
        textViewDateCreated = findViewById(R.id.textView_dateCreated)
        textViewDateModified = findViewById(R.id.textView_dateModified)

        catalogId = intent.getStringExtra("catalogId") ?: ""
        Log.d("CatalogItemWithoutUserActivity", "Catalog id: $catalogId")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        fetchCatalogDetails()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent= Intent(this, AllCatalogsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }

        btn_edit.setOnClickListener{
            val editIntent=Intent(this, SelectArticlesActivity::class.java)
            intent.putExtra("selectedCatalog", catalog)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(editIntent)
        }
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
                    val selectedCatalog = response.body()
                    if (selectedCatalog != null) {
                        updateUIWithCatalogDetails(selectedCatalog)
                        catalog = selectedCatalog
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
            textViewArticles.text = ""
        } else {
            fetchAndSetArticles(articlesList)
        }

        // Set services
        val servicesList = parseStringList(catalog.services)
        if (servicesList.isNullOrEmpty()) {
            textViewServices.text = ""
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
        textViewDateCreated.text = "${catalog.date_created}"
        textViewDateModified.text = "${catalog.date_modified}"
    }

    private fun fetchAndSetArticles(articleIds: List<String>?) {
        if (articleIds.isNullOrEmpty()) {
            textViewArticles.text = ""
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val articleNames = mutableListOf<String>()
        val remainingCount = AtomicInteger(articleIds.size)

        for (articleId in articleIds) {
            service.getArticles(loggedInUser.token).enqueue(object : Callback<List<Article>> {
                override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                    if (response.isSuccessful) {
                        val articles = response.body()
                        val article = articles?.find { it.id == articleId }
                        article?.let { articleNames.add(it.name) }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        textViewArticles.text = "${articleNames.joinToString(", ")}"
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
            textViewServices.text = ""
            return
        }

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val serviceNames = mutableListOf<String>()
        val remainingCount = AtomicInteger(serviceIds.size)

        for (serviceId in serviceIds) {
            service.getServices(loggedInUser.token).enqueue(object : Callback<List<Service>> {
                override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                    if (response.isSuccessful) {
                        val services = response.body()
                        val service = services?.find { it.id == serviceId }
                        service?.let { serviceNames.add(it.serviceName) }
                    }

                    if (remainingCount.decrementAndGet() == 0) {
                        textViewServices.text = "${serviceNames.joinToString(", ")}"
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