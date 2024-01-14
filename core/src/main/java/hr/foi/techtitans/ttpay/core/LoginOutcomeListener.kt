package hr.foi.techtitans.ttpay.core

import hr.foi.techtitans.ttpay.core.LoggedInUser

interface LoginOutcomeListener {
    fun onSuccessfulLogin(loggedInUser: LoggedInUser)
    fun onFailedLogin(reason: String?)
}