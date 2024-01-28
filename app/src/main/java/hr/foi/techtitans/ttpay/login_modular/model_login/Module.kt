package hr.foi.techtitans.ttpay.login_modular.model_login

import androidx.fragment.app.Fragment

data class Module (
    private var name: String? = null,
    private var fragmentClass: Class<out Fragment>? = null,
    private var activityClass: Class<*>? = null
) {

    fun getName(): String? {
        return name
    }

    fun getFragmentClass(): Class<out Fragment>? {
        return fragmentClass
    }

    fun getActivityClass(): Class<*>? {
        return activityClass
    }
}