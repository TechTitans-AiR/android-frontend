package hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement

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
import java.time.LocalDate
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.CreateUser
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.newUser
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser

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

        val dateCreated= LocalDate.now().toString()

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

            //userStatus-->always will be active when creating user

            val new = newUser(
                txtUsername.text.toString(),
                txtPassword.text.toString(),
                txtFirstname.text.toString(),
                txtLastname.text.toString(),
                txtEmail.text.toString(),
                txtAddress.text.toString(),
                txtPhone.text.toString(),
                birthDateString,
                dateCreated,
                null,
                selectedRole?.name,
                UserStatus.active.name,
            )
            val create= CreateUser()
            create.createNewUser(loggedInUser, this,new)//context,user
            Log.e("USER JE:", "${new.first_name} ")
        }
    }
}