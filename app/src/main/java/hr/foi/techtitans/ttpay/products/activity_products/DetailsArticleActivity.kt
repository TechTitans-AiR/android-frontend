package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsArticleActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    //UI elements
    private lateinit var itemNameTextView: TextView
    private lateinit var itemCategoryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var quantityInStockTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var materialTextView: TextView
    private lateinit var brandTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_article)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        itemNameTextView = findViewById(R.id.itemNameTextView)
        itemCategoryTextView = findViewById(R.id.itemCategoryTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        priceTextView = findViewById(R.id.priceTextView)
        quantityInStockTextView = findViewById(R.id.quantityInStockTextView)
        weightTextView = findViewById(R.id.weightTextView)
        materialTextView = findViewById(R.id.materialTextView)
        brandTextView = findViewById(R.id.brandTextView)
        progressBar = findViewById(R.id.progressBar)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val articleId = intent.getStringExtra("articleId")
        fetchArticleDetails(articleId)

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }

    private fun fetchArticleDetails(articleId: String?) {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticleDetails(articleId.orEmpty())
        call.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                hideLoading()

                if (response.isSuccessful) {
                    val article = response.body()
                    if (article != null) {
                        itemNameTextView.text = article.name
                        itemCategoryTextView.text = article.itemCategory.name
                        descriptionTextView.text = article.description
                        priceTextView.text = "${article.price} ${article.currency} "
                        quantityInStockTextView.text = "${article.quantity_in_stock}"
                        weightTextView.text = "${article.weight}"
                        materialTextView.text = article.material ?: "-"
                        brandTextView.text = article.brand
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
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
            .setMessage("Error fetching article details.")
            .setPositiveButton("Retry") { _, _ ->
                val articleId = intent.getStringExtra("articleId")
                fetchArticleDetails(articleId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}