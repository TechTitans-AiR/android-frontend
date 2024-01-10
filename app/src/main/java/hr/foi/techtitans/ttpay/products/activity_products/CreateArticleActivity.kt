package hr.foi.techtitans.ttpay.products.activity_products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser

class CreateArticleActivity : AppCompatActivity() {

    private lateinit var loadingDataProgressBar: ProgressBar
    private lateinit var actionBack: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    // Declare and initialize EditText and Spinner
    private lateinit var editTextArticleName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextBrand: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var editTextWeight: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextQuantityInStock : EditText
    private lateinit var btnCreateArticle:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllProductsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateProductActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }


        //initializing fields
        editTextArticleName= findViewById(R.id.editTextArticleName)
        editTextDescription= findViewById(R.id.editTextDescription)
        editTextBrand= findViewById(R.id.editTextArticleBrand)
        editTextPrice= findViewById(R.id.editTextPrice)
        editTextWeight= findViewById(R.id.editTextWeight)
        editTextQuantityInStock = findViewById(R.id.editTextQuantityInStock)
        spinnerCategory=findViewById(R.id.spinnerCategory)
        spinnerCurrency=findViewById(R.id.spinnerCurrency)
        btnCreateArticle=findViewById(R.id.btnCreateNewArticle)

        //set spinners
        setupCurrencySpinner()
        setupCategorySpinner()

        //TODO: implement method for calling endpoint

    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CNY", "INR") // Add more currencies as needed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
    }
    private fun setupCategorySpinner() {
        val categories = arrayOf("Fruit", "Kitchenware") // Add more currencies as needed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter=adapter
    }

    private fun showLoading() {
        loadingDataProgressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingDataProgressBar?.visibility = View.GONE
    }
}