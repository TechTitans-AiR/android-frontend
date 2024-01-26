package hr.foi.techtitans.ttpay

import hr.foi.techtitans.ttpay.transactions.model_transactions.Card
import hr.foi.techtitans.ttpay.transactions.model_transactions.NewTransaction
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class TransactionsUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: ServiceTransactionManagement

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        // Setup Retrofit with the MockWebServer base URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the ServiceTransactionManagement
        service = retrofit.create()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCreateTransactionCardSuccess() {
        // Enqueue a mock response for the createTransactionCard endpoint
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // Call createTransactionCard method
        val newTransaction = createMockNewTransaction()
        val call = service.createTransactionCard(newTransaction)
        val response = call.execute()

        // Check if the request was successful
        assert(response.isSuccessful)
        println("Transaction created successfully. \n$newTransaction")
    }

    @Test
    fun testCreateTransactionCardFailure() {
        // Enqueue a mock response for the createTransactionCard endpoint
        val mockResponse = MockResponse()
            .setResponseCode(500) // Simulate a server error
        mockWebServer.enqueue(mockResponse)

        // Call createTransactionCard method
        val newTransaction = createMockNewTransaction()
        val call = service.createTransactionCard(newTransaction)
        val response = call.execute()

        // Check if the request was unsuccessful (error response)
        assert(!response.isSuccessful)
        println("Error creating transaction.")
    }

    private fun createMockNewTransaction(): NewTransaction {
        // Replace this with the actual data you want to use for the test
        return NewTransaction(
            merchantId = "1090865344433",
            description = "Konzum transaction",
            amount = 100.0,
            card = Card(
                cardNumber = "1234567812345670",
                expirationDate = "12/23",
                balance = 100.0,
                cvc = 123
            ),
            currency = "EUR"
        )
    }
}
