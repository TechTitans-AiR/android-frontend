package hr.foi.techtitans.ttpay

import com.google.gson.Gson
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.products.model_products.ItemCategory
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: ServiceProducts
    private lateinit var loggedInUser: LoggedInUser

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Setup Retrofit with the MockWebServer base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the ServiceProducts
        service = retrofit.create(ServiceProducts::class.java)

        // Create a mock user for testing
        loggedInUser = LoggedInUser(
            username = "testUser",
            userId = "123456789",
            role = "admin",
            token = "fakeToken"
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulArticleUpdate() {
        // Enqueue a mock response for the getArticleDetails endpoint to simulate the original article
        val articleId = "123456789"
        val originalArticleDetails = mapOf(
            "id" to articleId,
            "itemCategory" to mapOf(
                    "id" to "87678923421",
            "name" to "Fruit"
        ),
            "name" to "Apple mini",
            "description" to "Original Description",
            "price" to 49.99,
            "currency" to "USD",
            "quantity_in_stock" to 100,
            "weight" to 1.5,
            "material" to "",
            "brand" to "local farms"
        )

        val mockOriginalResponse = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(originalArticleDetails))
        mockWebServer.enqueue(mockOriginalResponse)

        // Call getArticleDetails method to simulate fetching the original article
        val originalArticle = service.getArticleDetails(articleId, "Bearer ${loggedInUser.token}").execute().body()

        // Enqueue a mock response for the updateArticle endpoint
        val updatedFields = mapOf(
            "description" to "Updated Description",
            "price" to 99.99,
            "currency" to "USD",
            "quantity_in_stock" to 50,
            "weight" to 2.5
        )

        val mockUpdateResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockUpdateResponse)

        // Call updateArticle method
        val call = service.updateArticle(articleId, updatedFields, "Bearer ${loggedInUser.token}")
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)
        println("Article updated successfully. \nOriginal Article: $originalArticle\nUpdated Article Fields: $updatedFields")
    }


    @Test
    fun testFailedArticleUpdate() {
        // Enqueue a mock response for the getArticleDetails endpoint to simulate the original article
        val articleId = "987654321"
        val originalArticleDetails = mapOf(
            "id" to articleId,
            "itemCategory" to mapOf(
                "id" to "87678923421",
                "name" to "Fruit"
            ),
            "name" to "Apple mini",
            "description" to "Original Description",
            "price" to 49.99,
            "currency" to "USD",
            "quantity_in_stock" to 100,
            "weight" to 1.5,
            "material" to "",
            "brand" to "local farms"
        )

        val mockOriginalResponse = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(originalArticleDetails))
        mockWebServer.enqueue(mockOriginalResponse)

        // Call getArticleDetails method to simulate fetching the original article
        val originalArticle = service.getArticleDetails(articleId, "Bearer ${loggedInUser.token}").execute().body()

        // Enqueue a mock response for the updateArticle endpoint with an error code
        val updatedFields = mapOf(
            "description" to "Updated Description",
            "price" to 99.99,
            "currency" to "USD",
            "quantity_in_stock" to 50,
            "weight" to 2.5
        )

        val mockResponse = MockResponse()
            .setResponseCode(500) // Simulate a server error
        mockWebServer.enqueue(mockResponse)

        // Call updateArticle method
        val call = service.updateArticle(articleId, updatedFields, "Bearer ${loggedInUser.token}")
        val response = call.execute()

        // Check if the request was unsuccessful (error response)
        assert(!response.isSuccessful)
        println("Failed to update article. \nOriginal Article: $originalArticle")
    }

    @Test
    fun testSuccessfulServiceUpdate() {
        val serviceId = "123456789"

        // Simulate the original service details
        val originalServiceDetails = mapOf(
            "id" to serviceId,
            "serviceName" to "Original Service Name",
            "description" to "Original Description",
            "serviceProvider" to "Original Provider",
            "price" to 49.99,
            "currency" to "USD",
            "duration" to 20,
            "availability" to "Original Availability",
            "serviceLocation" to "Original Location",
            "durationUnit" to "hours"
        )

        val mockOriginalResponse = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(originalServiceDetails))
        mockWebServer.enqueue(mockOriginalResponse)

        // Call getServiceDetails method to simulate fetching the original service
        val originalService = service.getServiceDetails(serviceId, "Bearer ${loggedInUser.token}").execute().body()

        // Enqueue a mock response for the updateService endpoint
        val updatedFields = mapOf(
            "description" to "Updated Description",
            "price" to 99.99,
            "currency" to "USD",
            "duration" to 30,
            "durationUnit" to "days",
            "serviceLocation" to "Updated Location"
        )

        val mockUpdateResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockUpdateResponse)

        // Call updateService method
        val call = service.updateService(serviceId, updatedFields, "Bearer ${loggedInUser.token}")
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)

        // Print the original and updated service details
        println("Service updated successfully. \nOriginal Service: $originalService\nUpdated Service Fields: $updatedFields")
    }

    @Test
    fun testFailedServiceUpdate() {
        // Enqueue a mock response for the getServiceDetails endpoint to simulate the original service
        val serviceId = "987654321"
        val originalServiceDetails = mapOf(
            "id" to serviceId,
            "serviceName" to "Original Service Name",
            "description" to "Original Description",
            "serviceProvider" to "Original Provider",
            "price" to 49.99,
            "currency" to "USD",
            "duration" to 20,
            "availability" to "Original Availability",
            "serviceLocation" to "Original Location",
            "durationUnit" to "hours"
        )

        val mockOriginalResponse = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(originalServiceDetails))
        mockWebServer.enqueue(mockOriginalResponse)

        // Call getServiceDetails method to simulate fetching the original service
        val originalService = service.getServiceDetails(loggedInUser.token, serviceId).execute().body()

        // Enqueue a mock response for the updateService endpoint with an error code
        val updatedFields = mapOf(
            "description" to "Updated Description",
            "price" to 99.99,
            "currency" to "USD",
            "duration" to 30,
            "durationUnit" to "days",
            "serviceLocation" to "Updated Location"
        )

        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        val call = service.updateService(serviceId, updatedFields, "Bearer ${loggedInUser.token}")
        val response = call.execute()

        assert(!response.isSuccessful)
        println("Failed to update service. \nOriginal Service: $originalService")
    }

}
