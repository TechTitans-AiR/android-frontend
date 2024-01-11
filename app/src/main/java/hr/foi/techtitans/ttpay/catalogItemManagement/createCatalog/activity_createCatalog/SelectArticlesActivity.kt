package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.AddedArticleAdapter
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.SelectArticleAdapter
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.snackbar.Snackbar
import hr.foi.techtitans.ttpay.core.LoggedInUser


class SelectArticlesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerViewSelectArticles: RecyclerView
    private lateinit var selectArticleAdapter: SelectArticleAdapter

    private lateinit var addedArticleAdapter: AddedArticleAdapter
    private lateinit var recyclerViewAddedArticles: RecyclerView

    private val articles = mutableListOf<Article>()//article list

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_articles)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        recyclerViewSelectArticles = findViewById(R.id.recyclerView_select_articles)
        recyclerViewAddedArticles = findViewById(R.id.recyclerView_added_articles)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, CreateCatalogItemActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        progressBar = findViewById(R.id.loadingProgressBar)

        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            //Sending the list to the next screen
            val intent = Intent(this, SelectServicesActivity::class.java)
            Log.d("Articles: ",ArrayList(articles).toString())

            intent.putExtra("selected_articles", ArrayList(articles))
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        recyclerViewSelectArticles.layoutManager = LinearLayoutManager(this)

        selectArticleAdapter = SelectArticleAdapter(emptyList()) { article ->
            // Checking if the selected item is already added
            if (!articles.contains(article)) {
                articles.add(article)

                addedArticleAdapter.updateData(articles)

                showSnackbar("The article is added to the list of articles.")
            } else {
                // If the article is already added
                showSnackbar("The article is already added to the list of articles.")
            }
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
        val call = service.getArticles(loggedInUser.token)

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