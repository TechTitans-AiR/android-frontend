package hr.foi.techtitans.ttpay

import com.google.gson.Gson
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogsUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: ServiceCatalogItemManagement

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        // Setup Retrofit with the MockWebServer base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the ServiceCatalogItemManagement
        service = retrofit.create(ServiceCatalogItemManagement::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulCatalogSearch() {
        // Enqueue a mock response for the searchCatalogs endpoint
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(createMockCatalogsResponse())
        mockWebServer.enqueue(mockResponse)

        // Call searchCatalogs method with valid search parameters
        val searchParams = mapOf(
            "name" to "Friday",
            "user" to "2232345678",
            "service" to "9898656467",
            "article" to "1111111110"
        )

        val call = service.searchCatalogs(searchParams)
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)

        val catalogList = createMockCatalogsResponse()
        println("Catalog search results: \n$catalogList")
    }

    @Test
    fun testFailedCatalogSearch() {
        // Enqueue a mock response for the searchCatalogs endpoint with an error code
        val mockResponse = MockResponse()
            .setResponseCode(500) // Simulate a server error
        mockWebServer.enqueue(mockResponse)

        // Call searchCatalogs method with invalid search parameters
        val invalidSearchParams = mapOf("invalidParam" to "value")

        val call = service.searchCatalogs(invalidSearchParams)
        val response = call.execute()

        // Check if the request was unsuccessful (error response)
        assert(!response.isSuccessful)
        println("Error searching catalogs because of invalid search parameters.")
    }

    private fun createMockCatalogsResponse(): String {
        val catalogs = listOf(
            Catalog(
                id = "1",
                name = "Black Friday Transaction",
                articles = "[1111111110, 2221114456]",
                services = "9898656467, 9912312312",
                users = "[6666666785, 8888888767, 2232345678]",
                date_created = "2024-01-26T12:34:56",
                date_modified = "2024-01-26T12:34:56",
                disabled = false
            ),
            Catalog(
                id = "2",
                name = "Last Friday Transaction",
                articles = "[8877669909, 1111111110]",
                services = "9898656467, 7777777775",
                users = "[2232345678]",
                date_created = "2024-01-27T12:34:56",
                date_modified = "2024-01-27T12:34:56",
                disabled = true
            )
        )

        // Convert the list of Catalog objects to JSON string
        return Gson().toJson(catalogs)
    }
}