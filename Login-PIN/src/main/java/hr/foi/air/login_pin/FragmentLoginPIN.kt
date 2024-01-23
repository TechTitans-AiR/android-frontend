package hr.foi.air.login_pin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener

class FragmentLoginPIN(private val outcomeListener: LoginOutcomeListener) : Fragment() {

    private lateinit var editTextPIN: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBarPin: ProgressBar

    private lateinit var viewModel: FragmentLoginPINViewModel
    private var loginHandler : PINLoginHandler = PINLoginHandler()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_fragment_login_p_i_n, container, false)

        editTextPIN = view.findViewById(R.id.digitCodePIN)
        btnLogin = view.findViewById(R.id.btn_login_PIN_activity)
        progressBarPin=view.findViewById(R.id.progressBarPIN)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentLoginPINViewModel::class.java)


        btnLogin.setOnClickListener {
            val enteredPIN = editTextPIN.text.toString()

            showProgressBar()

            val loginTokenPIN = PINLoginToken(enteredPIN)
            loginHandler.handleLogin(loginTokenPIN, outcomeListener)

        }
    }
    private fun showProgressBar() {
        progressBarPin.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBarPin.visibility = View.GONE
    }



}