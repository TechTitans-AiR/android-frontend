package hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserRole
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserStatus
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.time.LocalDate
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.newUser
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class CreateNewMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_merchant)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AllMerchantsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateNewMerchant - loggedInUser: ", loggedInUser.toString())
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        //Spinner- UserRole
        val userRoleSpinner: Spinner = findViewById(R.id.spinner_user_role)
        val userRoleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(UserRole.admin.name, UserRole.merchant.name)
        )
        userRoleSpinner.adapter = userRoleAdapter

        //ArrayAdapter for UserStatus Spinner
        val userStatusSpinner: Spinner = findViewById(R.id.spinner_user_status)
        val userStatusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(UserStatus.active.name, UserStatus.inactive.name, UserStatus.blocked.name)
        )
        userStatusSpinner.adapter = userStatusAdapter
        userStatusSpinner.setSelection(0)
        userStatusSpinner.isClickable = false
        userStatusSpinner.isFocusable = false
        userStatusSpinner.isFocusableInTouchMode = false

        // data from fields
        val txtFirstname: EditText =findViewById(R.id.editText_first_name)
        val txtLastname:EditText=findViewById(R.id.editText_last_name)
        val txtBirthDate:EditText=findViewById(R.id.editText_date_of_birth)
        val txtAddress:EditText=findViewById(R.id.editText_address)
        val txtPhone:EditText=findViewById(R.id.editText_phone)
        val txtEmail:EditText=findViewById(R.id.editText_email)
        val txtUsername:EditText=findViewById(R.id.editText_username)
        val txtPassword:EditText=findViewById(R.id.editText_password)
        val txtPIN:EditText=findViewById(R.id.editText_pin)


        //button for creating user --> POST createUser
        val btnCreateUser: Button = findViewById(R.id.btn_create_merchant)

        btnCreateUser.setOnClickListener {

            val birthDateString=txtBirthDate.text.toString()

            //userRole
            val selectedRoleString =
                userRoleSpinner.selectedItem.toString() // user role from spinner
            //first i make role null
            var selectedRole: UserRole? = null

            if (selectedRoleString.isEmpty()) {//check if the string of role is empty (role not selected)
                Toast.makeText(this, "User role must be selected!", Toast.LENGTH_LONG).show()
            } else {//role is selected
                //then when selected role from spinner, role is admin or merchant
                selectedRole = when (selectedRoleString) {
                    UserRole.admin.name -> UserRole.admin // when selected admin, role will be admin
                    UserRole.merchant.name -> UserRole.merchant
                    else -> null
                }
            }

            if (!isValidEmail(txtEmail.text.toString())) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if(txtUsername.text.toString().isNotEmpty() &&
                txtPassword.text.toString().isNotEmpty() &&
                txtFirstname.text.toString().isNotEmpty() &&
                txtLastname.text.toString().isNotEmpty() &&
                txtUsername.text.toString().isNotEmpty() &&
                txtEmail.text.toString().isNotEmpty()){

                val newUserData = newUser(
                    txtUsername.text.toString(),
                    txtPassword.text.toString(),
                    txtFirstname.text.toString(),
                    txtLastname.text.toString(),
                    txtEmail.text.toString(),
                    txtAddress.text.toString(),
                    txtPhone.text.toString(),
                    birthDateString,
                    selectedRole?.name,
                    userStatusSpinner.selectedItem.toString(),
                    txtPIN.text.toString()
                )
                Log.d("CreateNewActivity", "Search JSON: ${Gson().toJson(newUserData)}")
                createNewUser(loggedInUser, this,newUserData)//context,user
                Log.e("USER JE:", newUserData.toString())
            }else{
                Toast.makeText(this, "Firstname, Lastname, E-mail, Username, Password and PIN are neccessary fields. ", Toast.LENGTH_SHORT).show()

            }



        }
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    private fun createNewUser(loggedInUser: LoggedInUser, context: Context, newUser: newUser){
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)

        // call for method createUser
        val call = service.createNewUser(loggedInUser.token, newUser)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                Log.d("Response create user: ", response.toString())

                if(response.isSuccessful) { // successful response
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val createdUserString = responseBody.string()

                        // Prikazivanje string poruke iz odgovora
                        Log.d("Userrrr: ", createdUserString)

                        if (createdUserString.contains("User added successfully")) {
                            val intent = Intent(context, AllMerchantsActivity::class.java)
                            intent.putExtra("loggedInUser", loggedInUser)
                            intent.putExtra("username", newUser.username)
                            startActivity(intent)
                            finish()
                        }

                        Toast.makeText(context, createdUserString, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, "ERROR: Response body is null!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "ERROR: User not created!", Toast.LENGTH_SHORT).show()
                }
                Log.d("Response create user: ", response.toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable){
                Log.e("NetworkError", "Error: ${t.message}") // show error message in Logcat
                Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}