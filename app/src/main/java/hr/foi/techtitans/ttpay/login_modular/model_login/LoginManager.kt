package hr.foi.techtitans.ttpay.login_modular.model_login

import android.content.Context
import androidx.fragment.app.Fragment
import hr.foi.air.login_pin.FragmentLoginPIN
import hr.foi.air.login_pin.PINLoginHandler
import hr.foi.techtitans.ttpay.core.LoginHandler
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_UsernamePassword.FragmentLoginUsernamePass
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginHandler
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUser

class LoginManager (private val context: Context) {

    fun getModules(): List<LoginHandler> {
        val modules = mutableListOf<LoginHandler>()
        modules.add(UsernamePasswordLoginHandler())
        modules.add(PINLoginHandler())
        //when adding new modules it needs to be declared here
        return modules
    }
    fun getFragmentForModule(loginHandler: LoginHandler, context: Context, outcomeListener: LoginOutcomeListener): Fragment? {
        try {
            val fragmentClass = loginHandler.getFragmentClass(context, outcomeListener)

            return fragmentClass
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}