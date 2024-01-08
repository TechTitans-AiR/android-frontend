package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateArticleActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    // UI elements
    private lateinit var progressBar: ProgressBar
    private lateinit var itemNameEditText: EditText
    private lateinit var itemCategoryEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var currencyEditText: EditText
    private lateinit var quantityInStockEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var materialEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnEditData: Button

    // Original values that can be changed
    private lateinit var originalDescription: String
    private lateinit var originalPrice: String
    private lateinit var originalCurrency: String
    private lateinit var originalQuantityInStock: String
    private lateinit var originalWeight: String

    private var isEditMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_article)

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar)
        itemNameEditText = findViewById(R.id.itemNameEditText)
        itemCategoryEditText = findViewById(R.id.itemCategoryEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        priceEditText = findViewById(R.id.priceEditText)
        currencyEditText = findViewById(R.id.currencyEditText)
        quantityInStockEditText = findViewById(R.id.quantityInStockEditText)
        weightEditText = findViewById(R.id.weightEditText)
        materialEditText = findViewById(R.id.materialEditText)
        brandEditText = findViewById(R.id.brandEditText)
        btnCancel = findViewById(R.id.btnCancel)
        btnEditData = findViewById(R.id.btnEditData)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("UpdateArticleActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }

        // Get article details based on the provided articleId
        val articleId = intent.getStringExtra("articleId")
        if (articleId != null) {
            getArticleDetails(articleId)
        }

        btnCancel.setOnClickListener {
            // If in edit mode, revert changes
            if (isEditMode) {
                revertChanges()
            }
        }

        btnEditData.setOnClickListener {
            toggleEditMode()
        }

    }

    private fun getArticleDetails(articleId: String) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticleDetails(articleId)
        call.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    val article = response.body()
                    updateUIWithArticleDetails(article)
                } else {
                    Toast.makeText(this@UpdateArticleActivity, "Failed to retrieve article details", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Log.e("UpdateArticleActivity", "Error: ${t.message}", t)
                Toast.makeText(this@UpdateArticleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun updateUIWithArticleDetails(article: Article?) {
        // Update the UI elements with the fetched article details
        if (article != null) {
            itemNameEditText.setText(article.name)
            itemCategoryEditText.setText(article.itemCategory.name)
            descriptionEditText.setText(article.description)
            priceEditText.setText(article.price.toString())
            currencyEditText.setText(article.currency)
            quantityInStockEditText.setText(article.quantityInStock.toString())
            weightEditText.setText(article.weight?.toString() ?: "")
            materialEditText.setText(article.material ?: "")
            brandEditText.setText(article.brand)
        } else {
            itemNameEditText.text.clear()
            itemCategoryEditText.text.clear()
            descriptionEditText.text.clear()
            priceEditText.text.clear()
            currencyEditText.text.clear()
            quantityInStockEditText.text.clear()
            weightEditText.text.clear()
            materialEditText.text.clear()
            brandEditText.text.clear()
            Toast.makeText(this@UpdateArticleActivity, "Article details not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            enableEditMode()
        } else {
            // If exiting edit mode, save or revert changes
            if (changesMade()) {
                saveChanges()
            } else {
                revertChanges()
            }
            disableEditMode()
        }
    }

    private fun enableEditMode() {
        // Enable editing for specified fields
        descriptionEditText.isEnabled = true
        priceEditText.isEnabled = true
        currencyEditText.isEnabled = true
        quantityInStockEditText.isEnabled = true
        weightEditText.isEnabled = true

        // Save original values for later comparison
        originalDescription = descriptionEditText.text.toString()
        originalPrice = priceEditText.text.toString()
        originalCurrency = currencyEditText.text.toString()
        originalQuantityInStock = quantityInStockEditText.text.toString()
        originalWeight = weightEditText.text.toString()
    }

    private fun disableEditMode() {
        // Disable editing for specified fields
        itemNameEditText.isEnabled = false
        itemCategoryEditText.isEnabled = false
        descriptionEditText.isEnabled = false
        priceEditText.isEnabled = false
        currencyEditText.isEnabled = false
        quantityInStockEditText.isEnabled = false
        weightEditText.isEnabled = false
        materialEditText.isEnabled = false
        brandEditText.isEnabled = false
    }

    private fun revertChanges(){
    }

    private fun changesMade(): Boolean {
        // Check if any changes have been made
        return (originalDescription != descriptionEditText.text.toString() ||
                originalPrice != priceEditText.text.toString() ||
                originalCurrency != currencyEditText.text.toString() ||
                originalQuantityInStock != quantityInStockEditText.text.toString() ||
                originalWeight != weightEditText.text.toString())
    }

    private fun saveChanges() {
    }

    private fun collectUpdatedFields(): Map<String, Any> {
        val fieldsMap = mutableMapOf<String, Any>()

        if (changesMade()) {
            // Collect updated fields
            fieldsMap["description"] = descriptionEditText.text.toString()
            fieldsMap["price"] = priceEditText.text.toString().toDouble()
            fieldsMap["currency"] = currencyEditText.text.toString()
            fieldsMap["quantityInStock"] = quantityInStockEditText.text.toString().toInt()
            fieldsMap["weight"] = weightEditText.text.toString().toDouble()
        }

        return fieldsMap
    }

}