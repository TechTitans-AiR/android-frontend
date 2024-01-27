package hr.foi.techtitans.ttpay.core


interface LoginOutcomeListener {
    fun onSuccessfulLogin(loggedInUser: LoggedInUser)
    fun onFailedLogin(reason: String?)
}