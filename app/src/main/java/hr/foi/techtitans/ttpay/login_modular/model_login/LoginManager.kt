package hr.foi.techtitans.ttpay.login_modular.model_login

import android.content.Context
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUser

class LoginManager (private val context: Context) {

    fun getModules(): List<Module> {
        val modules = mutableListOf<Module>()
        modules.add(Module("Login - Username and Password", LoginUser::class.java))
        modules.add(Module("Login - PIN", LoginUser::class.java))
        //when adding new modules it needs to be declared here
        return modules
    }
}