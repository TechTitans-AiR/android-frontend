package hr.foi.techtitans.ttpay.login_UsernamePassword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener

class FragmentLoginUsernamePass (private val outcomeListener: LoginOutcomeListener) : Fragment() {

    private var loginHandler : UsernamePasswordLoginHandler=UsernamePasswordLoginHandler()
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: FragmentLoginUsernamePassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_fragment_login_username_pass, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        usernameEditText = view.findViewById(R.id.editTextUsername)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        loginButton = view.findViewById(R.id.btn_login_login_activity)

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[FragmentLoginUsernamePassViewModel::class.java]

        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            showProgressBar()

            val loginToken = UsernamePasswordLoginToken(enteredUsername, enteredPassword)
            loginHandler.handleLogin(loginToken, outcomeListener)

        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }


}