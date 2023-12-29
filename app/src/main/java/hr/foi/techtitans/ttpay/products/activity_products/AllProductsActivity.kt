package hr.foi.techtitans.ttpay.products.activity_products

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProductsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private var progressBar: ProgressBar? = null
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var recyclerViewServices: RecyclerView
    private var articleAdapter = ArticleAdapter(emptyList())
    private val serviceAdapter = ServiceAdapter(emptyList())

    private var listArticles:List<Article> = emptyList()
    private var listServices:List<Service> = emptyList()
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)

        progressBar = findViewById(R.id.loadingProgressBar)

        userUsername = intent.getStringExtra("username") ?: ""

        recyclerViewArticles = findViewById(R.id.recyclerView_all_articles)
        recyclerViewServices = findViewById(R.id.recyclerView_all_services)

        recyclerViewArticles.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.layoutManager = LinearLayoutManager(this)

        articleAdapter = ArticleAdapter(listArticles)


        recyclerViewArticles.adapter = articleAdapter
        recyclerViewServices.adapter = serviceAdapter



        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE



        fetchArticles()
        fetchServices()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusIconProductsClick(view: View) {
        val intent = Intent(this, CreateProductActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    fun fetchArticles() {
        listArticles= emptyList()
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticles()

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    listArticles = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Articles fetched successfully: $listArticles")
                    articleAdapter.updateData(listArticles)
                    articleAdapter.notifyDataSetChanged()
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun fetchServices() {
        listServices= emptyList()
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServices()

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    listServices = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Services fetched successfully: $listServices")
                    serviceAdapter.updateData(listServices)
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


    private fun showLoading() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar?.visibility = View.GONE
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchArticles()
                fetchServices()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

}