package hr.foi.techtitans.ttpay


import android.content.Intent
import androidx.test.core.app.ActivityScenario
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginHandler
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginToken
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUsernamePassword
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentCaptor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleInstrumentedTest {


    @Test
    fun testFailedLoginCallback() {
        // Create a mock of LoginOutcomeListener
        val mockListener = mock(LoginOutcomeListener::class.java)

        // Call onFailedLogin with a non-null reason
        mockListener.onFailedLogin("Failed login!")

        // Verify that onFailedLogin was called with the correct argument
        verify(mockListener, times(1)).onFailedLogin("Failed login!")

    }

}