package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.transactions.model_transactions.ShoppingCartItem
import hr.foi.techtitans.ttpay.transactions.model_transactions.ShoppingCartAdapter
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTransactionActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private val shoppingCartItems = mutableListOf<ShoppingCartItem>()
    private lateinit var articlesAdapter: ShoppingCartAdapter
    private lateinit var servicesAdapter: ShoppingCartAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var btn_pay: Button

    private lateinit var loggedInUser: LoggedInUser
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_transaction)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        totalAmountTextView = findViewById(R.id.tv_total_amount)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)
        btn_pay = findViewById(R.id.btn_pay)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val role = loggedInUser.role
            if (role == "admin") {
                val intent = Intent(this, AllTransactionsActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                startActivity(intent)
            } else if (role == "merchant") {
                val intent = Intent(this, AllTransactionsMerchantActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                startActivity(intent)
            }
            finish()
        }

        // Set recyclerview and total amount
        Log.d("CreateTransactionActivity", "onCreate: Setting up RecyclerViews and total amount")
        setupRecyclerViews()
        if (loggedInUser.role == "admin") {
            fetchAllArticlesForAdmin()
            fetchAllServicesForAdmin()
        } else if (loggedInUser.role == "merchant") {
            fetchAllArticlesByUser()
            fetchAllServicesByUser()
        }
        else {
            Toast.makeText(this, "User role is not fetched.", Toast.LENGTH_SHORT).show()
        }

        btn_pay.setOnClickListener {
            Log.d("CreateTransactionActivity", "btn_pay onClick: Transferring data to TransactionSummaryActivity")

            // Transfer data on TransactionSummaryActivity
            val intent = Intent(this, TransactionSummaryActivity::class.java)

            // Transfer shoppingCartItems
            intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
            Log.d("CreateTransactionActivity", "btn_pay onClick: shoppingCartItems: $shoppingCartItems")

            // transfer totalAmount
            val totalAmount = shoppingCartItems.sumByDouble { it.quantity * it.unitPrice }
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateTransactionActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("totalAmount", totalAmount)
            Log.d("CreateTransactionActivity", "btn_pay onClick: totalAmount: $totalAmount")

            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateTransactionActivity - LoggedInUser",loggedInUser.toString())

            Log.d("CreateTransactionActivity", "btn_pay onClick: Starting TransactionSummaryActivity")
            // Transfer on TransactionSummaryActivity
            startActivity(intent)
        }
    }

    private fun updateTotalAmount() {
        val totalAmount = shoppingCartItems.sumByDouble { it.quantity * it.unitPrice }
        totalAmountTextView.text = "Total Amount: €${"%.2f".format(totalAmount)}"
    }

    private fun setupRecyclerViews() {
        // RecyclerView articles
        val articlesRecyclerView = findViewById<RecyclerView>(R.id.recycler_articles)
        articlesRecyclerView.layoutManager = LinearLayoutManager(this)
        articlesAdapter = ShoppingCartAdapter(shoppingCartItems.filter { it.isArticle }.toMutableList()) { item, unitPrice ->
            addToCart(item, unitPrice)
        }
        articlesRecyclerView.adapter = articlesAdapter

        // RecyclerView services
        val servicesRecyclerView = findViewById<RecyclerView>(R.id.recycler_services)
        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
        servicesAdapter = ShoppingCartAdapter(shoppingCartItems.filter { !it.isArticle }.toMutableList()) { item, unitPrice ->
            addToCart(item, unitPrice)
        }
        servicesRecyclerView.adapter = servicesAdapter
    }

    private fun addToCart(item: ShoppingCartItem, unitPrice: Double) {
        item.unitPrice = unitPrice
        updateTotalAmount()
        setupRecyclerViews()
    }

    private fun fetchAllArticlesForAdmin() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticles(loggedInUser.token)

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    Log.d("CreateTransactionActivity", "Articles fetched successfully: $articles")

                    // add articles in the shoppingCartItems with initial unit price
                    val shoppingCartArticles = articles.map { article ->
                        ShoppingCartItem(article.name, 0, article.price, true)
                    }
                    shoppingCartItems.addAll(shoppingCartArticles)

                    // Update presenting data
                    articlesAdapter.updateData(shoppingCartArticles)
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

    private fun fetchAllServicesForAdmin() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServices(loggedInUser.token)

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Services fetched successfully: $services")

                    // add services in the shoppingCartItems with initial unit price
                    val shoppingCartServices = services.map { service ->
                        ShoppingCartItem(service.serviceName, 0, service.price, false)
                    }
                    shoppingCartItems.addAll(shoppingCartServices)

                    // update presenting data
                    servicesAdapter.updateData(shoppingCartServices)
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


    private fun fetchAllArticlesByUser() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getAllArticlesByUserInAllEnabledCatalogs(loggedInUser.token, loggedInUser.userId)

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    Log.d("CreateTransactionActivity", "Articles fetched successfully: $articles")

                    // add articles in the shoppingCartItems with initial unit price
                    val shoppingCartArticles = articles.map { article ->
                        ShoppingCartItem(article.name, 0, article.price, true)
                    }
                    shoppingCartItems.addAll(shoppingCartArticles)

                    // Update presenting data
                    articlesAdapter.updateData(shoppingCartArticles)
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

    private fun fetchAllServicesByUser() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getAllServicesByUserInAllEnabledCatalogs(loggedInUser.token, loggedInUser.userId)

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    Log.d("AllProductsActivity", "Services fetched successfully: $services")

                    // add services in the shoppingCartItems with initial unit price
                    val shoppingCartServices = services.map { service ->
                        ShoppingCartItem(service.serviceName, 0, service.price, false)
                    }
                    shoppingCartItems.addAll(shoppingCartServices)

                    // update presenting data
                    servicesAdapter.updateData(shoppingCartServices)
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
                fetchAllArticlesByUser()
                fetchAllServicesByUser()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}