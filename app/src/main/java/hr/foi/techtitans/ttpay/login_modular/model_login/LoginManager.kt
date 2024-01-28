package hr.foi.techtitans.ttpay.login_modular.model_login

import android.content.Context
import androidx.fragment.app.Fragment
import hr.foi.air.login_pin.FragmentLoginPIN
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_UsernamePassword.FragmentLoginUsernamePass
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUser

class LoginManager (private val context: Context) {

    fun getModules(): List<Module> {
        val modules = mutableListOf<Module>()
        modules.add(Module("Login - Username and Password",FragmentLoginUsernamePass::class.java, LoginUser::class.java))
        modules.add(Module("Login - PIN",FragmentLoginPIN::class.java, LoginUser::class.java))
        //when adding new modules it needs to be declared here
        return modules
    }
    fun getFragmentForModule(module: Module, context: Context, outcomeListener: LoginOutcomeListener): Fragment? {
        try {
            val fragmentClass = module.getFragmentClass()

            val constructor = fragmentClass?.getConstructor(Context::class.java, LoginOutcomeListener::class.java)

            if (constructor != null && fragmentClass != null) {
                val fragmentInstance = constructor.newInstance(context, outcomeListener)

                return fragmentInstance as? Fragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}