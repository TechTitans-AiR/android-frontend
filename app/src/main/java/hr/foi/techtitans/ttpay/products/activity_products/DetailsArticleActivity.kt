package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class DetailsArticleActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    private lateinit var itemNameTextView: TextView
    private lateinit var itemCategoryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var quantityInStockTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var materialTextView: TextView
    private lateinit var brandTextView: TextView
    private lateinit var currencyTextView: TextView
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
        currencyTextView = findViewById(R.id.currencyTextView)
        progressBar = findViewById(R.id.progressBar)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val articleId = intent.getStringExtra("articleId")

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}