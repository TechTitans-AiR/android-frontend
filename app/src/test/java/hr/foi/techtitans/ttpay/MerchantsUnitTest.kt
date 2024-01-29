package hr.foi.techtitans.ttpay

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserRole
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserStatus
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MerchantsUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: ServiceAccountManagement

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val gson = Gson()

        // Setup Retrofit with the MockWebServer base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        // Create an instance of the ServiceAccountManagement
        service = retrofit.create(ServiceAccountManagement::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetUsersSuccess() {
        // Enqueue a mock response for the getUsers endpoint
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(getMockUsersJson(2)) // Define mock JSON response for an empty list (1) or for the full list of users (2)
        mockWebServer.enqueue(mockResponse)

        // Call getUsers method
        val call = service.getUsers("fakeToken")
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)

        // Parse the response body
        val users: List<User>? = response.body()

        // Additional check for an empty or null list
        if (users.isNullOrEmpty()) {
            // Handle the case when the list is empty or null
            println("User list is empty or null.")
        } else {
            // Verify that the response contains the expected list of users
            println("Users are fetched. $users")
        }
    }

    private fun getMockUsersJson(scenario: Int): String {
        // Define mock JSON response using Gson
        return when (scenario) {
            1 -> Gson().toJson(emptyList<User>(), object : TypeToken<List<User>>() {}.type)
            2 -> {
                val users = listOf(
                    User("1", "user1", "password1", "John", "Doe", "johndoe@example.com", "123 Tranquil Lane", "+1234567890", "01.01.1990", "2022-01-01 12:00:00", null, UserRole.admin, UserStatus.active, "1234"),
                    User("2", "user2", "password2", "Ian", "Doe", "ivandoe@example.com", "123 Lane", "+1234567899", "01.01.1990", "2022-01-01 12:00:00", null, UserRole.admin, UserStatus.active, "1233"),
                )
                Gson().toJson(users, object : TypeToken<List<User>>() {}.type)
            }
            else -> throw IllegalArgumentException("Invalid scenario: $scenario")
        }
    }

    @Test
    fun testGetUsersFailure() {
        // Enqueue a mock response for the getUsers endpoint
        val mockResponse = MockResponse()
            .setResponseCode(500) // Simulate a server error
        mockWebServer.enqueue(mockResponse)

        // Call getUsers method
        val call = service.getUsers("fakeToken")
        val response = call.execute()

        // Check if the request was unsuccessful (error response)
        assert(!response.isSuccessful)

        // Handle the case when the request is unsuccessful (show error dialog)
        println("Error fetching users. Show error dialog.")
    }

    @Test
    fun testDeleteUserSuccess() {

        // Simulate the userId to delete specific user
        val userIdToDelete = "11332290809"

        // Enqueue a mock response for the deleteUser endpoint
        val mockResponse = MockResponse()
            .setResponseCode(204) // Simulate a successful deletion
        mockWebServer.enqueue(mockResponse)

        // Call deleteUser method
        val call = service.deleteUser("fakeToken", userIdToDelete)
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)

        // Print the user ID if deletion is successful
        println("User with ID $userIdToDelete deleted successfully.")
    }

    @Test
    fun testDeleteUserFailure() {

        // Simulated userId to delete specific user
        val userIdToDelete = "11332290809"

        // Enqueue a mock response for the deleteUser endpoint
        val mockResponse = MockResponse()
            .setResponseCode(500) // Simulate a server error
        mockWebServer.enqueue(mockResponse)

        // Call deleteUser method
        val call = service.deleteUser("fakeToken", userIdToDelete)
        val response = call.execute()

        // Check if the request was unsuccessful
        assert(!response.isSuccessful)

        // Print the user ID if deletion fails
        println("Failed to delete user with ID $userIdToDelete.")
    }
}
