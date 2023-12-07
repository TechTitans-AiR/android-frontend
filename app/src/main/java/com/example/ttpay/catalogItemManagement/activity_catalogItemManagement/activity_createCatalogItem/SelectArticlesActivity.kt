package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.model.AddedArticleAdapter
import com.example.ttpay.model.Article
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.SelectArticleAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.snackbar.Snackbar


class SelectArticlesActivity : AppCompatActivity() {
    private lateinit var selectArticleAdapter: SelectArticleAdapter
    private lateinit var addedArticleAdapter: AddedArticleAdapter
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var recyclerViewSelectArticles: RecyclerView
    private lateinit var recyclerViewAddedArticles: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val articles = mutableListOf<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_articles)

        recyclerViewSelectArticles = findViewById(R.id.recyclerView_select_articles)
        recyclerViewAddedArticles = findViewById(R.id.recyclerView_added_articles)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, CreateCatalogItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        progressBar = findViewById(R.id.loadingProgressBar)

        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            //Sending the list to the next screen
            val intent = Intent(this, SelectServicesActivity::class.java)
            intent.putExtra("selected_articles", ArrayList(articles))
            startActivity(intent)
            finish()
        }

        recyclerViewSelectArticles.layoutManager = LinearLayoutManager(this)

        selectArticleAdapter = SelectArticleAdapter(emptyList()) { article ->
            //Adding the selected article to the list of articles
            articles.add(article)

            //Updating the display of added articles
            addedArticleAdapter.updateData(articles)

            //Snackbar message
            showSnackbar("The article is added to the list of articles.")
        }

        recyclerViewAddedArticles.layoutManager = LinearLayoutManager(this)
        addedArticleAdapter = AddedArticleAdapter(articles) { position ->
            // Deleting the selected article from the list of articles
            articles.removeAt(position)

            //Updating the display of added articles
            addedArticleAdapter.updateData(articles)

            //Snackbar message
            showSnackbar("The article is deleted from the list of articles.")
        }

        recyclerViewSelectArticles.adapter = selectArticleAdapter
        recyclerViewAddedArticles.adapter = addedArticleAdapter

        fetchArticles()
    }

    private fun fetchArticles() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)//za catalog_item_management
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticles()

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    selectArticleAdapter.updateData(articles)
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
                fetchArticles()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
        snackbar.show()
    }

}