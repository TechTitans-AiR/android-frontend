package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
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
    private lateinit var edtDescriptionCash: EditText
    private lateinit var edtDescriptionCard: EditText
    private lateinit var edtCardNumber: EditText
    private lateinit var edtExpirationDate: EditText
    private lateinit var edtBalance: EditText
    private lateinit var edtCvc: EditText
    private lateinit var progressBar: ProgressBar

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
        edtCashAmount = findViewById(R.id.edt_cash_amount)
        edtDescriptionCash = findViewById(R.id.edt_description_cash)

        layoutCard = findViewById(R.id.llBankCard)
        edtDescriptionCard = findViewById(R.id.edt_description_bank_card)
        edtCardNumber = findViewById(R.id.edt_card_number)
        edtExpirationDate = findViewById(R.id.edt_expiration_date)
        edtBalance = findViewById(R.id.edt_balance)
        edtCvc = findViewById(R.id.edt_cvc)

        progressBar = findViewById(R.id.progressBar)

        completePayment = findViewById(R.id.btn_complete_payment)
        completePayment.setOnClickListener {
            handleCompletePayment()
        }

        radioGroupPaymentOptions.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentOptionChange(checkedId)
        }
    }

    private fun handlePaymentOptionChange(checkedId: Int) {
        when (checkedId) {
            R.id.radioCash -> {
                layoutCash.visibility = View.VISIBLE
            }
            R.id.radioBankCard -> {
                layoutCard.visibility = View.VISIBLE
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

    private fun handleCashPayment() {
        val cashAmount = edtCashAmount.text.toString().toDoubleOrNull()
        if (cashAmount != null && cashAmount >= 0) {
            if (cashAmount < totalAmount) {
                completePayment.isEnabled = false
                showToast("Entered amount is insufficient.")
                Log.d("PaymentOptionsActivity", "Entered amount is insufficient.")
                // Hide progress bar
                progressBar.visibility = View.GONE
                return
            }
        } else {
            completePayment.isEnabled = false
            showToast("Please enter a valid cash amount.")
            Log.d("PaymentOptionsActivity", "Invalid cash amount entered.")
            // Hide progress bar
            progressBar.visibility = View.GONE
            return
        }

        // Set Card to an empty object for cash payment
        val cardForTransaction = Card()
        completePayment.isEnabled = true

        val description = edtDescriptionCash.text.toString()
        val currency = "EUR"
        val newTransaction = NewTransaction(loggedInUser.userId, description, totalAmount, cardForTransaction, currency)
            sendTransactionCashToBackend(newTransaction, getYourCashAmount())
    }

    private fun handleCardPayment() {
        // Set real Card for card payment
        val cardForTransaction = Card(
            edtCardNumber.text.toString(),
            edtExpirationDate.text.toString(),
            totalAmount,
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

    private fun showErrorDialog(username: String) {
        showToast("Error fetching data for $username")
        Log.d("PaymentOptionsActivity", "Error fetching data for $username")
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
                if (response.isSuccessful) {
                    val newTransactionResponse = response.body()
                    if (newTransactionResponse != null) {
                        handleTransactionCreationSuccess(newTransactionResponse, cashAmount)
                        Log.d("PaymentOptionsActivity", "Transaction created successfully.")
                    }
                } else {
                    Log.e("PaymentOptionsActivity", "Error creating transaction. Code: ${response.code()}")

                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentOptionsActivity", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<NewTransaction>, t: Throwable) {
                Log.d("PaymentOptionsActivity", "Communication error with backend.")
            }
        })
    }

    private fun sendTransactionCardToBackend(newTransaction: NewTransaction) {
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)

        val call = service.createTransactionCard(newTransaction)

        call.enqueue(object : Callback<NewTransaction> {
            override fun onResponse(
                call: Call<NewTransaction>,
                response: Response<NewTransaction>
            ) {
                if (response.isSuccessful) {
                    val newTransactionResponse = response.body()
                    if (newTransactionResponse != null) {
                        handleTransactionCreationSuccess(newTransactionResponse, 0.0) // Assuming 0.0 for cashAmount for card transactions
                        Log.d("PaymentOptionsActivity", "Transaction created successfully.")
                    }
                } else {
                    Log.e("PaymentOptionsActivity", "Error creating transaction. Code: ${response.code()}")

                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentOptionsActivity", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<NewTransaction>, t: Throwable) {
                Log.d("PaymentOptionsActivity", "Communication error with backend.")
            }
        })
    }

    private fun handleTransactionCreationSuccess(newTransaction: NewTransaction, cashAmount: Double) {
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
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}