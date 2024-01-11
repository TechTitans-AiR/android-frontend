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
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.ItemCategory
import hr.foi.techtitans.ttpay.products.model_products.NewArticle
import hr.foi.techtitans.ttpay.products.model_products.NewService
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateArticleActivity : AppCompatActivity() {

    private lateinit var loadingDataProgressBar: ProgressBar
    private lateinit var btnBack : ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    // Declare and initialize EditText and Spinner
    private lateinit var editTextArticleName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextBrand: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextMaterial: EditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var editTextWeight: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextQuantityInStock : EditText
    private lateinit var btnCreateArticle:Button
    private  var itemCategories:List<ItemCategory>? = emptyList()
    private  lateinit var itemCategoryId:ItemCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        loadingDataProgressBar = findViewById(R.id.loadingData)
        btnBack = findViewById(R.id.back_button)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        itemCategoryId= ItemCategory("","")

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        btnBack.setOnClickListener {
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
        editTextMaterial=findViewById(R.id.editTextMaterial)
        spinnerCategory=findViewById(R.id.spinnerCategory)
        spinnerCurrency=findViewById(R.id.spinnerCurrency)
        btnCreateArticle=findViewById(R.id.btnCreateNewArticle)



        //set spinners
        setupCurrencySpinner()
        setupCategorySpinner()

        val selectedCategory = spinnerCategory.selectedItem.toString()
        getCategoryId(selectedCategory)

        btnCreateArticle.setOnClickListener{
             // Fetch data from EditText and Spinner
            val articleName = editTextArticleName.text.toString()
            val description = editTextDescription.text.toString()
            val price = editTextPrice.text.toString().toDouble()
            val quantityInStock= editTextQuantityInStock.text.toString().toInt()
            val weight = editTextWeight.text.toString().toDouble()
            val material = editTextMaterial.text.toString()
            val brand = editTextBrand.text.toString()
            val currency = spinnerCurrency.selectedItem.toString()


            Log.d("Item category:" , itemCategoryId.toString())

            // Get the JWT token from the loggedInUser
            val jwtToken = loggedInUser.token

            // Create a NewService object
            val newArticle = NewArticle(
                articleName,
                itemCategoryId.id,
                description,
                price,
                quantityInStock,
                weight,
                material,
                brand,
                currency,
            )

            Log.d("newArticle: ", newArticle.toString())
            createNewArticle(jwtToken,newArticle)
        }

    }

    private fun createNewArticle(jwtToken: String, newArticle: NewArticle) {
        showLoading()

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.createArticle(jwtToken, newArticle)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                hideLoading()
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Article created successfully", Toast.LENGTH_SHORT).show()
                    intent.putExtra("loggedInUser", loggedInUser)
                    intent.putExtra("username", userUsername)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Error creating article. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, "Error creating article. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCategoryId(selectedCategory: String) {

        showLoading()

        val retrofit=RetrofitClient.getInstance(8081)
        val service= retrofit.create(ServiceProducts :: class.java)
        val call=service.getItemCategory()

        call.enqueue(object:Callback<List<ItemCategory>> {
            override fun onResponse(call: Call<List<ItemCategory>>, response: Response<List<ItemCategory>>) {
                hideLoading()
                if (response.isSuccessful) {
                    itemCategories=response.body()
                    Log.d("List categories: ", itemCategories.toString())
                    for (item in itemCategories.orEmpty()) {
                        if (item.name == selectedCategory) {
                            itemCategoryId = item
                            Log.d("Selected Category ID: ", itemCategoryId.id)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Error fetching categories. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ItemCategory>>, t: Throwable) {
                hideLoading()
                Toast.makeText(applicationContext, "Failed fetching categories. Please try again.", Toast.LENGTH_SHORT).show()

            }
        })
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