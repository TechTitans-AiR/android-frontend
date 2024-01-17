package hr.foi.techtitans.ttpay.login_modular.model_login

data class Module ( private var name: String? = null, private var activityClass: Class<*>? = null) {

    fun Module(name: String?, activityClass: Class<*>?) {
        this.name = name
        this.activityClass = activityClass
    }

    fun getName(): String? {
        return name
    }

    fun getActivityClass(): Class<*>? {
        return activityClass
    }
}
