package hr.foi.techtitans.ttpay

import org.junit.Assert.*
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

class TTPayUnitTest {

    @Test
    fun testFailedLoginCallback() {
        // Create a mock of LoginOutcomeListener
        val mockListener = mock(LoginOutcomeListener::class.java)

        // Call onFailedLogin with a non-null reason
        mockListener.onFailedLogin("Failed login!")

        // Use ArgumentCaptor to capture the argument
        val captor = ArgumentCaptor.forClass(String::class.java)

        // Verify that onFailedLogin was called with the correct argument
        verify(mockListener, times(1)).onFailedLogin(captor.capture())

        // Assert the captured argument
        assertEquals("Failed login!", captor.value)
    }

}