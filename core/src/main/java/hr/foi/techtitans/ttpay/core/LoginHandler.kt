package hr.foi.techtitans.ttpay.core

import android.content.Context
import androidx.fragment.app.Fragment

interface LoginHandler {
    fun handleLogin(loginToken: LoginToken, loginListener: LoginOutcomeListener)

    fun getFragmentClass(context: Context, outcomeListener: LoginOutcomeListener): Fragment
    fun getName(): String?
}