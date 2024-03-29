package hr.foi.techtitans.ttpay.products.activity_products

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.ArticleAdapter
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.model_products.ServiceAdapter
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProductsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private var progressBar: ProgressBar? = null
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var serviceAdapter: ServiceAdapter

    private var listArticles:List<Article> = emptyList()
    private var listServices:List<Service> = emptyList()
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)

        progressBar = findViewById(R.id.loadingProgressBar)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        articleAdapter= ArticleAdapter(emptyList(), loggedInUser, true)
        serviceAdapter= ServiceAdapter(emptyList(), loggedInUser, true)

        recyclerViewArticles = findViewById(R.id.recyclerView_all_articles)
        recyclerViewServices = findViewById(R.id.recyclerView_all_services)

        recyclerViewArticles.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.layoutManager = LinearLayoutManager(this)

        articleAdapter = ArticleAdapter(listArticles, loggedInUser)

        recyclerViewArticles.adapter = articleAdapter
        recyclerViewServices.adapter = serviceAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        fetchArticles()
        fetchServices()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllProductsActivity - LoggedInUser",loggedInUser.toString())
            startActivity(intent)
            finish()
        }
    }

    fun onPlusIconProductsClick(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        // Set the dialog title and message
        dialogBuilder.setTitle("Choose")
            .setMessage("Do you want to create an article or a service?")
        // Create buttons for creating an article, creating a service, and canceling
        dialogBuilder.setPositiveButton("Create Article") { _, _ ->
            // Open the activity for creating articles
            val intent = Intent(this, CreateArticleActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            startActivity(intent)
        }
        dialogBuilder.setNegativeButton("Create Service") { _, _ ->
            // Open the activity for creating services
            val intent = Intent(this, CreateServiceActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            startActivity(intent)
        }
        dialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
            // Dismiss the dialog if canceled
            dialog.dismiss()
        }
        // Show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    fun fetchArticles() {
        listArticles= emptyList()
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticles(loggedInUser.token)

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    listArticles = response.body() ?: emptyList()
                    if (listArticles.isNotEmpty()) {
                        articleAdapter.updateData(listArticles)
                    } else {
                        Toast.makeText(
                            this@AllProductsActivity,
                            "There are no articles yet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showErrorDialogArticles()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                hideLoading()
                showErrorDialogArticles()
            }
        })
    }

    private fun fetchServices() {
        listServices = emptyList()
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServices(loggedInUser.token)

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    listServices = response.body() ?: emptyList()
                    if (listServices.isNotEmpty()) {
                        serviceAdapter.updateData(listServices)
                    } else {
                        Toast.makeText(
                            this@AllProductsActivity,
                            "There are no services yet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showErrorDialogServices()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                hideLoading()
                showErrorDialogServices()
            }
        })
    }


    private fun showLoading() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar?.visibility = View.GONE
    }

    private fun showErrorDialogArticles() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching articles.")
            .setPositiveButton("Retry") { _, _ ->
                fetchArticles()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorDialogServices() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching services.")
            .setPositiveButton("Retry") { _, _ ->
                fetchServices()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onRestart() {
        super.onRestart()
        fetchArticles()
        fetchServices()
    }

}