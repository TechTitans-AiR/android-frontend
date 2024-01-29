package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.DetailedCatalogItemActivity
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.AddedArticleAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.SelectArticleAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectArticlesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerViewSelectArticles: RecyclerView
    private  var selectArticleAdapter: SelectArticleAdapter?=null

    private lateinit var addedArticleAdapter: AddedArticleAdapter
    private lateinit var recyclerViewAddedArticles: RecyclerView

    private var articles = mutableListOf<Article>()//article list

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private var catalog:Catalog?=Catalog(null, "", "", "", "", null, null, false)

    private  var currentCatalog:Catalog?=Catalog(null, "", "", "", "", null, null, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_articles)

        recyclerViewSelectArticles = findViewById(R.id.recyclerView_select_articles)
        recyclerViewAddedArticles = findViewById(R.id.recyclerView_added_articles)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        catalog=intent.getParcelableExtra("selectedCatalog")
        Log.d("SelectArticles - Selected catalog: ", catalog.toString())

         currentCatalog=catalog


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
        recyclerViewSelectArticles.adapter = selectArticleAdapter

        recyclerViewAddedArticles.layoutManager = LinearLayoutManager(this)
        addedArticleAdapter = AddedArticleAdapter(articles) { position ->

            // Deleting the selected article from the list of articles
            articles.removeAt(position)

            //Updating the display of added articles
            addedArticleAdapter.updateData(articles)

            // Snackbar message
            showSnackbar("The article is deleted from the list of articles.")

        }
        recyclerViewAddedArticles.adapter = addedArticleAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            if(catalog==null){
                val intent = Intent(this, CreateCatalogItemActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, DetailedCatalogItemActivity::class.java)
                intent.putExtra("selectedCatalog", catalog)
                intent.putExtra("catalogId", catalog?.id)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)
                startActivity(intent)
                finish()
            }

        }

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchArticles()

        continueButton = findViewById(R.id.btn_continue_select_services)
        continueButton.setOnClickListener {
            //Sending the list to the next screen
            val intent = Intent(this, SelectServicesActivity::class.java)
            Log.d("Articles: ",ArrayList(articles).toString())
            if(catalog!=null){
                intent.putExtra("selectedCatalog", catalog)
            }
            intent.putExtra("selected_articles", ArrayList(articles))
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        Log.d("Initial Articles Size", selectArticleAdapter?.itemCount.toString())

    }

    private fun initAddedArticleAdapter() {
        addedArticleAdapter = AddedArticleAdapter(articles) { position ->
            // Deleting the selected article from the list of articles
            articles.removeAt(position)

            //Updating the display of added articles
            addedArticleAdapter.updateData(articles)

            //Snackbar message
            showSnackbar("The article is deleted from the list of articles.")
        }
        recyclerViewAddedArticles.adapter = addedArticleAdapter
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
                    Log.d("Articles Response", articles.toString())

                    Log.d("Catalog:", catalog.toString())

                    if(catalog!=null){
                        handleAllArticles(articles)
                    }else{
                        selectArticleAdapter?.updateData(articles)
                    }
                    Log.d("RecyclerView Size", recyclerViewSelectArticles.adapter?.itemCount.toString())
                } else {
                    Log.e("Articles Response", "Unsuccessful: ${response.code()}")
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }
    private fun handleAllArticles(allArticles: List<Article>) {

        val selectedArticles = allArticles.filter { article ->
            currentCatalog?.articles?.contains(article.id) == true
        }
        Log.d("selectedArticles.filter",selectedArticles.toString())


        articles=selectedArticles.toMutableList()
        addedArticleAdapter.updateData(selectedArticles)
        selectArticleAdapter?.updateData(allArticles.filterNot{selectedArticles.contains(it)})

        if (recyclerViewAddedArticles.adapter == null) {
            initAddedArticleAdapter()
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