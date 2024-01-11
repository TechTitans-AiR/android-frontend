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
        edtCashAmount = findViewById(R.id.edt_cash_amount)
        edtDescriptionCash = findViewById(R.id.edt_description_cash)
        layoutCash = findViewById(R.id.llCash)
        layoutCard = findViewById(R.id.llBankCard)

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
        }

        val cardForTransaction = if (radioGroupPaymentOptions.checkedRadioButtonId == R.id.radioCash) {
            // set Card to empty object if radioCash is checked
            Card()
        } else {
            // else set real Card
            Card("stvarniBrojKartice", "2023-12", 3, 444)
        }


        completePayment.isEnabled = true

        fetchUserId(userUsername) { merchantId ->
            val description_cash = edtDescriptionCash.text.toString()
            val currency = "EUR"
            val newTransaction = NewTransaction(merchantId, description_cash, totalAmount, cardForTransaction, currency)
            sendTransactionToBackend(newTransaction, getYourCashAmount())
        }
    }

    private fun getYourCashAmount(): Double {
        return edtCashAmount.text.toString().toDoubleOrNull() ?: 0.0
    }

    private fun fetchUserId(username: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers(loggedInUser.token)

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    if (user != null) {
                        val merchantId = user.id.toString()
                        Log.d("PaymentOptionsActivity", "Fetched merchant ID: $merchantId")
                        callback.invoke(merchantId)
                    } else {
                        showErrorDialog(username)
                    }
                } else {
                    showErrorDialog(username)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showErrorDialog(username)
            }
        })
    }

    private fun showErrorDialog(username: String) {
        showToast("Error fetching data for $username")
        Log.d("PaymentOptionsActivity", "Error fetching data for $username")
    }

    private fun sendTransactionToBackend(newTransaction: NewTransaction, cashAmount: Double) {
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)

        val call = service.createTransaction(newTransaction)

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