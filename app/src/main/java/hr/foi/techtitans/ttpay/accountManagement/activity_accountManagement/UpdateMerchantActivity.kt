package hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement


import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserRole
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserStatus
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.updateUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var userID: String


    //Prepare fields for write data
    private lateinit var editTxtFirstName: EditText
    private lateinit var editTxtLastName: EditText
    private lateinit var editTxtDateOfBirth: EditText
    private lateinit var editTxtAddress: EditText
    private lateinit var editTxtPhone: EditText
    private lateinit var editTxtEmail: EditText
    private lateinit var editTxtUsername: EditText
    private lateinit var editTxtPassword: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerRole: Spinner


    private var user: updateUser = updateUser("", "", "", "", "", null, null, null, "", null)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_merchant)


        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        Log.d("UpdateMerchantActivity - LoggedInUser: ", loggedInUser.toString())
        userUsername = intent.getStringExtra("username") ?: ""

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            finish()
        }

        //Initializing fields for data
        editTxtFirstName = findViewById(R.id.editText_first_name)
        editTxtLastName = findViewById(R.id.editText_last_name)
        editTxtDateOfBirth = findViewById(R.id.editText_date_of_birth)
        editTxtAddress = findViewById(R.id.editText_address)
        editTxtPhone = findViewById(R.id.editText_phone)
        editTxtEmail = findViewById(R.id.editText_email)
        editTxtUsername = findViewById(R.id.editText_username)
        editTxtPassword = findViewById(R.id.editText_password)
        spinnerStatus = findViewById(R.id.spinner_user_status)
        spinnerRole = findViewById(R.id.spinner_user_role)


        //For userID
        userID = intent.getStringExtra("userId") ?: ""
        Log.d("UpdateMerchantActivity userID:", userID)


        //Lists for spinner value
        val userRoleList = arrayOf(
            UserRole.merchant,
            UserRole.admin
        )

        val userStatusList = arrayOf(
            UserStatus.active,
            UserStatus.inactive,
            UserStatus.blocked
        )

        var oldRole: UserRole?= UserRole.merchant
        var oldStatus: UserStatus?= UserStatus.active
        var dateCreated=""

        //call endpoint for user details
        Log.d("FetchMerchant: ", userID)
        fetchMerchants(loggedInUser, userID) { fetchedUser ->
            //User is found and his data will be updated
            if (fetchedUser != null && fetchedUser.id==userID) {
                editTxtFirstName.setText(fetchedUser.first_name)
                editTxtLastName.setText(fetchedUser.last_name)
                editTxtDateOfBirth.setText(fetchedUser.date_of_birth)
                editTxtAddress.setText(fetchedUser.address)
                editTxtPhone.setText(fetchedUser.phone)
                editTxtEmail.setText(fetchedUser.email)
                editTxtUsername.setText(fetchedUser.username)
                editTxtPassword.setText(fetchedUser.password)
                dateCreated=fetchedUser.date_created


                // Adapter initialization for user role spinner
                val userRoleAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    userRoleList.map { it.name } // Use enum names for display
                )
                spinnerRole.adapter = userRoleAdapter
                oldRole=fetchedUser.userRole
                Log.d("User Role: ", fetchedUser.userRole?.name.toString())

                // Adapter initialization for user status spinner
                val userStatusAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    userStatusList.map { it.name } // Use enum names for display
                )
                spinnerStatus.adapter = userStatusAdapter
                oldStatus=fetchedUser.userStatus
                Log.d("User status: ", fetchedUser.userStatus?.name.toString())
            } else {
                Toast.makeText(
                    this@UpdateMerchantActivity,
                    "User details are null.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("fetchMerchants(userId)", "User details are null!")
            }
        }



        // For the user role spinner
        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = userRoleList[position]
                user.user_role = selectedRole.id
                Log.d("New role: ", user.user_role.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Old role: ", oldRole?.id.toString())
                user.user_role = oldRole?.id ?: "DefaultUserRole"
            }
        }

        // For the user status spinner
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = userStatusList[position]
                user.user_status = selectedStatus.id
                Log.d("New status: ", user.user_status.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Old status: ", oldStatus?.id.toString())
                user.user_status = oldStatus?.id ?: "DefaultUserStatus"
            }

    }

        //When clicking button update
        val btnUpdate:Button=findViewById(R.id.btn_update_merchant)
        btnUpdate.setOnClickListener{

            //fetch NEW data from fields
            val txtFirstName: String = editTxtFirstName.text.toString()
            val txtLastName: String = editTxtLastName.text.toString()
            val txtDateOfBirth: String = editTxtDateOfBirth.text.toString()
            val txtAddress: String = editTxtAddress.text.toString()
            val txtPhone: String = editTxtPhone.text.toString()
            val txtEmail: String = editTxtEmail.text.toString()
            val txtUsername: String = editTxtUsername.text.toString()
            val txtPassword: String = editTxtPassword.text.toString()


            var newDataUser = updateUser(
                txtUsername,
                txtPassword,
                txtFirstName,
                txtLastName,
                txtEmail,
                txtAddress,
                txtPhone,
                txtDateOfBirth,
                user.user_role,
                user.user_status
            )
            Log.d("new data: ", newDataUser.toString())

            updateMerchantData(loggedInUser, this, newDataUser, userID)
            val intent = Intent(this@UpdateMerchantActivity, AllMerchantsActivity::class.java)
            intent.putExtra("loggedInUser",loggedInUser)
            intent.putExtra("username", userUsername)
            Toast.makeText(this@UpdateMerchantActivity, "Updating merchant ${newDataUser.first_name}!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }


    }
}

    private fun fetchMerchants(loggedInUser: LoggedInUser, userId: String, callback: (User?) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(loggedInUser.token, userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user: User? = if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.d("Response message is: ", response.message())
                    null
                }
                callback(user)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("Failed getting user information: ", t.message.toString())
                callback(null)
            }
        })
    }

fun updateMerchantData(loggedInUser: LoggedInUser, context: Context, updatedData: updateUser, userID: String) {
    val retrofit = RetrofitClient.getInstance(8080) // Replace 8080 with your specific port
    val service = retrofit.create(ServiceAccountManagement::class.java)

    // Call for method updateMerchantData
    val call = service.updateMerchantData(loggedInUser.token, userID, updatedData)

    call.enqueue(object : Callback<updateUser> {
        override fun onResponse(call: Call<updateUser>, response: Response<updateUser>) {
            if (response.isSuccessful) {
                val updatedUser: updateUser? = response.body()
                if (updatedUser != null) {
                    Log.d("UpdatedUser: ", "$updatedUser")
                    Toast.makeText(context, "Updated user ${updatedUser.first_name} ${updatedUser.last_name}!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("User: ", "$updatedUser")
                    Toast.makeText(context, "ERROR: User object is null!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("Response: ", "${response.message()}")
                Toast.makeText(context, "ERROR: User not updated!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<updateUser>, t: Throwable) {
            Log.e("NetworkError", "Error: ${t.message}")
            Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
        }
    })
}

