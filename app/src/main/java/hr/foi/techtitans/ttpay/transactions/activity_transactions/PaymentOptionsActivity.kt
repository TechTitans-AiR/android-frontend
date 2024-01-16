package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.model_transactions.NewTransaction
import hr.foi.techtitans.ttpay.transactions.model_transactions.ShoppingCartItem
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import hr.foi.techtitans.ttpay.transactions.model_transactions.Card
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var shoppingCartItems: MutableList<ShoppingCartItem>
    private var totalAmount: Double = 0.0
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var completePayment: Button
    private lateinit var radioGroupPaymentOptions: RadioGroup
    private lateinit var edtCashAmount: EditText
    private lateinit var edtTotalAmount: EditText
    private lateinit var edtDescriptionCash: EditText
    private lateinit var edtDescriptionCard: EditText
    private lateinit var edtCardNumber: EditText
    private lateinit var edtExpirationDate: EditText
    private lateinit var edtBalance: EditText
    private lateinit var edtCvc: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var cancelPayment: Button
    private lateinit var btnCheckCashAmount: Button
    private lateinit var btnCheckValidateCardData: Button

    private lateinit var layoutCash: LinearLayout
    private lateinit var layoutCard: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        shoppingCartItems =
            intent.getSerializableExtra("shoppingCartItems") as MutableList<ShoppingCartItem>
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        Log.d("PaymentOptionsActivity", "onCreate: username: $userUsername")
        Log.d("PaymentOptionsActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("PaymentOptionsActivity", "onCreate: totalAmount: $totalAmount")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        radioGroupPaymentOptions = findViewById(R.id.radioGroupPaymentOptions)

        layoutCash = findViewById(R.id.llCash)
        edtCashAmount = findViewById(R.id.edt_cash)
        edtDescriptionCash = findViewById(R.id.edt_description_cash)
        edtTotalAmount = findViewById(R.id.edt_ttl_amount)
        btnCheckCashAmount = findViewById(R.id.btn_check_cash_amount)

        layoutCard = findViewById(R.id.llBankCard)
        edtDescriptionCard = findViewById(R.id.edt_description_bank_card)
        edtCardNumber = findViewById(R.id.edt_card_number)
        edtExpirationDate = findViewById(R.id.edt_expiration_date)
        edtBalance = findViewById(R.id.edt_balance)
        edtCvc = findViewById(R.id.edt_cvc)
        btnCheckValidateCardData = findViewById(R.id.btn_check_validate_card_data)

        progressBar = findViewById(R.id.progressBar)

        cancelPayment = findViewById(R.id.btn_cancel_payment)
        cancelPayment.visibility = View.GONE
        cancelPayment.setOnClickListener {
            navigateBackToHomeActivity()
        }

        btnCheckCashAmount.setOnClickListener {
            checkCashAmountValidity()
        }

        btnCheckValidateCardData.setOnClickListener {
            validateCardData()
        }

        completePayment = findViewById(R.id.btn_complete_payment)
        completePayment.visibility = View.GONE
        completePayment.setOnClickListener {
            handleCompletePayment()
        }

        radioGroupPaymentOptions.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentOptionChange(checkedId)
        }

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllTransationsActivity - LoggedInUser", loggedInUser.toString())
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }

    private fun handlePaymentOptionChange(checkedId: Int) {
        when (checkedId) {
            R.id.radioCash -> {
                layoutCash.visibility = View.VISIBLE
                edtCashAmount.visibility = View.VISIBLE
                btnCheckCashAmount.visibility = View.VISIBLE
                edtTotalAmount.visibility = View.VISIBLE
                edtTotalAmount.setText(totalAmount.toString())
                edtTotalAmount.isEnabled = false
                layoutCard.visibility = View.GONE
                completePayment.visibility = View.GONE
                cancelPayment.visibility = View.VISIBLE
            }
            R.id.radioBankCard -> {
                layoutCard.visibility = View.VISIBLE
                layoutCash.visibility = View.GONE
                edtCashAmount.visibility = View.GONE
                edtTotalAmount.visibility = View.GONE
                btnCheckCashAmount.visibility = View.GONE
                edtBalance.setText(totalAmount.toString())
                edtBalance.isEnabled = false
                completePayment.visibility = View.GONE
                cancelPayment.visibility = View.VISIBLE
            }
        }
    }

    private fun handleCompletePayment() {
        // Show progress bar
        progressBar.visibility = View.VISIBLE

        if (radioGroupPaymentOptions.checkedRadioButtonId == R.id.radioCash) {
            handleCashPayment()
        } else if (radioGroupPaymentOptions.checkedRadioButtonId == R.id.radioBankCard) {
            handleCardPayment()
        }
    }

    private fun checkCashAmountValidity(): Boolean {
        val cashAmount = edtCashAmount.text.toString().toDoubleOrNull()

        if (cashAmount != null && cashAmount >= 0) {
            if (cashAmount >= totalAmount) {
                showToast("Cash amount is valid.")
                completePayment.visibility = View.VISIBLE
                completePayment.isEnabled = true
                return true
            } else {
                showToast("Entered amount is insufficient.")
            }
        } else {
            showToast("Please enter a valid cash amount.")
        }

        // If execution reaches here, it means the cash amount is not valid
        completePayment.isEnabled = false
        // Hide progress bar
        progressBar.visibility = View.GONE
        return false
    }

    private fun validateCardData(): Boolean {
        val cardNumber = edtCardNumber.text.toString()
        val expirationDate = edtExpirationDate.text.toString()
        val cvc = edtCvc.text.toString()

        val errorMessages = mutableListOf<String>()

        // Validate card number
        if (cardNumber.length != 16) {
            errorMessages.add("Card number must have exactly 16 digits.")
        }

        // Validate expiration date (assuming the format should be yyyy-mm)
        val regex = Regex("""^\d{4}-\d{2}$""")
        if (!regex.matches(expirationDate)) {
            errorMessages.add("Invalid expiration date format. Use yyyy-mm.")
        }

        // Validate CVC
        if (cvc.length != 3) {
            errorMessages.add("CVC must have exactly 3 digits.")
        }

        // If there are any errors, display them and hide the "COMPLETE PAYMENT" button
        if (errorMessages.isNotEmpty()) {
            showToast(errorMessages.joinToString("\n"))
            completePayment.visibility = View.GONE
            return false
        }

        // If all validations pass, set the "COMPLETE PAYMENT" button to visible
        completePayment.visibility = View.VISIBLE
        showToast("All card details are in the correct format.")
        return true
    }



    @SuppressLint("SuspiciousIndentation")
    private fun handleCashPayment() {

        // Set Card to an empty object for cash payment
        val cardForTransaction = Card()

        val description = edtDescriptionCash.text.toString()
        val currency = "EUR"
        val newTransaction = NewTransaction(loggedInUser.userId, description, totalAmount, cardForTransaction, currency)
            sendTransactionCashToBackend(newTransaction, getYourCashAmount())
    }

    private fun handleCardPayment() {
        // Set real Card for card payment
        val cardForTransaction = Card(
            edtCardNumber.text.chunked(4).joinToString(" "),
            edtExpirationDate.text.toString(),
            edtBalance.text.toString().toDouble(),
            edtCvc.text.toString().toIntOrNull() ?: 0
        )

        completePayment.isEnabled = true

        val description = edtDescriptionCard.text.toString()
        val currency = "EUR"
        val newTransaction = NewTransaction(loggedInUser.userId, description, totalAmount, cardForTransaction, currency)
        sendTransactionCardToBackend(newTransaction)
    }

    private fun getYourCashAmount(): Double {
        return edtCashAmount.text.toString().toDoubleOrNull() ?: 0.0
    }

    private fun sendTransactionCashToBackend(newTransaction: NewTransaction, cashAmount: Double) {
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)

        val call = service.createTransactionCash(newTransaction)

        call.enqueue(object : Callback<NewTransaction> {
            override fun onResponse(
                call: Call<NewTransaction>,
                response: Response<NewTransaction>
            ) {
                // Hide progress bar
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val newTransactionResponse = response.body()
                    if (newTransactionResponse != null) {
                        handleTransactionCashCreationSuccess(newTransactionResponse, cashAmount)
                        Log.d("PaymentOptionsActivity", "Transaction created successfully.")
                    }
                } else {
                    Log.e("PaymentOptionsActivity", "Error creating transaction. Code: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentOptionsActivity", "Error Body: $errorBody")
                    navigateBackToHomeActivity()
                    Toast.makeText(applicationContext, "Error creating transaction. $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewTransaction>, t: Throwable) {
                // Hide progress bar
                progressBar.visibility = View.GONE
                Log.d("PaymentOptionsActivity", "Communication error with backend.")
                Toast.makeText(applicationContext, "Communication error with backend. ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendTransactionCardToBackend(newTransaction: NewTransaction) {
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)

        val call = service.createTransactionCard(newTransaction)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Hide progress bar
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    handleTransactionCardCreationSuccess(newTransaction)
                    Log.d("PaymentOptionsActivity", "Transaction created successfully.")
                    Toast.makeText(applicationContext, "Transaction created successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentOptionsActivity", "Error creating transaction. Code: ${response.code()}, Error Body: $errorBody")
                    navigateBackToHomeActivity()
                    // Display the error response in a Toast message
                    Toast.makeText(applicationContext, "Error creating transaction. $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Hide progress bar
                progressBar.visibility = View.GONE
                Log.e("PaymentOptionsActivity", "Communication error with backend.", t)
                navigateBackToHomeActivity()
                Toast.makeText(applicationContext, "Communication error with backend. ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleTransactionCashCreationSuccess(newTransaction: NewTransaction, cashAmount: Double) {
        Log.d("PaymentOptionsActivity", "Cash Amount: $cashAmount")
        Log.d("PaymentOptionsActivity", "Total Amount: $totalAmount")
        val differenceAmount = cashAmount - totalAmount

        val intent = Intent(this, TransactionCompletionActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        intent.putExtra("newTransaction", newTransaction)
        intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
        intent.putExtra("totalAmount", totalAmount)
        intent.putExtra("username", userUsername)
        intent.putExtra("differenceAmount", differenceAmount)
        intent.putExtra("isCardPayment", false)
        startActivity(intent)
    }

    private fun handleTransactionCardCreationSuccess(newTransaction: NewTransaction) {
        val intent = Intent(this, TransactionCompletionActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        intent.putExtra("newTransaction", newTransaction)
        intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
        intent.putExtra("username", userUsername)
        intent.putExtra("isCardPayment", true)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateBackToHomeActivity() {
        val role = loggedInUser.role // Assuming role is a property in LoggedInUser class

        if (role == "admin") {
            // Open AdminHomeActivity
            val intent = Intent(this, TransactionsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        } else if (role == "merchant") {
            // Open MerchantHomeActivity
            val intent = Intent(this, AllTransactionsMerchantActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }
    }
}