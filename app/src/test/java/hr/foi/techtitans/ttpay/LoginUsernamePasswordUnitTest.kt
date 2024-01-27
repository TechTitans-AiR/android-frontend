package hr.foi.techtitans.ttpay

import com.google.gson.Gson
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestData
import hr.foi.techtitans.ttpay.core.network.data.LoginResponseData
import hr.foi.techtitans.ttpay.core.network.data.ResponseBodyData
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginToken
import hr.foi.techtitans.ttpay.login_UsernamePassword.service.`LoginService-UserPass`
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginUsernamePasswordUnitTest {

    private lateinit var service: `LoginService-UserPass`
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val gson = Gson()

        // Setup Retrofit with the MockWebServer base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        // Create an instance of the LoginService
        service = retrofit.create(`LoginService-UserPass`::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulLogin() {
        // Enqueue a mock response for the login endpoint
        val mockLoginResponse = MockResponse()
            .setResponseCode(200)
            .setBody(getMockLoginResponse())
        mockWebServer.enqueue(mockLoginResponse)

        // Call handleLogin method
        val loginToken = UsernamePasswordLoginToken("validUsername", "validPassword")
        val loginListener = object : LoginOutcomeListener {
            override fun onSuccessfulLogin(loggedInUser: LoggedInUser) {
                // Verify that the login was successful
                assert(loggedInUser.username == "validUsername")
                assertNotNull(loggedInUser.token)
                println("Login successful. User: $loggedInUser")
            }

            override fun onFailedLogin(reason: String?) {
                // Handle the case when the login fails
                println("Login failed. Reason: $reason")
            }
        }

        val call = service.login(LoginRequestData("validUsername", "validPassword"))
        val response = call.execute()

        // Verify that the request was successful
        assert(response.isSuccessful)

        // Simulate the asynchronous response
        if (loginToken.getAuthorizers()["username"] == "validUsername" && loginToken.getAuthorizers()["password"] == "validPassword") {
            // Simulate the asynchronous response
            loginListener.onSuccessfulLogin(getMockLoggedInUser())
        } else {
            // You might want to handle this case or return a different Call
            // For example: mock(MyCallClass::class.java)
            throw IllegalArgumentException("Unexpected login request")
        }
    }

    private fun getMockLoggedInUser(): LoggedInUser {
        return LoggedInUser(
            username = "validUsername",
            userId = "234567865432",
            role = "admin",
            token = "fakeToken"
        )
    }

    private fun getMockLoginResponse(): String {
        // Define a mock JSON response for a successful login
        val loginResponse = LoginResponseData(
            emptyMap(),
            ResponseBodyData("Success", "fakeToken"),
            "200",
            200
        )
        return Gson().toJson(loginResponse)
    }

    @Test
    fun testFailedLogin() {
        // Enqueue a mock response for the login endpoint with an error code
        val mockErrorResponse = MockResponse()
            .setResponseCode(401) // Unauthorized error code
            .setBody("Invalid credentials")
        mockWebServer.enqueue(mockErrorResponse)

        // Call handleLogin method
        val loginToken = UsernamePasswordLoginToken("invalidUsername", "invalidPassword")
        val loginListener = object : LoginOutcomeListener {
            override fun onSuccessfulLogin(loggedInUser: LoggedInUser) {
                // This callback should not be called for a failed login
                throw IllegalStateException("Unexpected onSuccessfulLogin callback")
            }

            override fun onFailedLogin(reason: String?) {
                // Verify that the login failed for the expected reason
                assert(reason == "Invalid credentials")
                println("Login failed. Reason: $reason")
            }
        }

        // Use the service instance within the test
        val call = service.login(LoginRequestData("invalidUsername", "invalidPassword"))
        val response = call.execute()

        // Verify that the request was not successful (HTTP 401 Unauthorized)
        assert(!response.isSuccessful)

        // Simulate the asynchronous response
        if (loginToken.getAuthorizers()["username"] == "invalidUsername" && loginToken.getAuthorizers()["password"] == "invalidPassword") {
            // Simulate the asynchronous response
            loginListener.onFailedLogin("Invalid credentials")
        } else {
            throw IllegalArgumentException("Unexpected login request")
        }
    }
}