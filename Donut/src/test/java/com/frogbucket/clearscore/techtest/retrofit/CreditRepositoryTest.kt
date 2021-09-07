package com.frogbucket.clearscore.techtest.retrofit

import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse

class CreditRepositoryTest {
    @Test
    public fun whenDataEmpty_noCrash() {
        val mockWebServer = createServer("empty.json", 200)
        mockWebServer.start()

        val repository = createRepository(mockWebServer)

        runBlocking {
            val userData = repository.getUserData()
            assertTrue(userData.isSuccessful)
        }

        mockWebServer.shutdown()
    }

    @Test
    public fun whenNotFound_notSuccessful() {
        val mockWebServer = createServer("notFound.html", 404)
        mockWebServer.start()

        val repository = createRepository(mockWebServer)

        runBlocking {
            val userData = repository.getUserData()
            assertFalse(userData.isSuccessful)
        }

        mockWebServer.shutdown()
    }

    @Test
    public fun whenDataIsValid_readsValidData() {
        val mockWebServer = createServer("valid.json", 200)
        mockWebServer.start()

        val repository = createRepository(mockWebServer)

        runBlocking {
            val userData = repository.getUserData()
            assertTrue(userData.isSuccessful)
            MatcherAssert.assertThat(userData.isSuccessful, `is`(true))
            MatcherAssert.assertThat(userData.body(), notNullValue())
            MatcherAssert.assertThat(userData.body()?.creditReportInfo, notNullValue())
            MatcherAssert.assertThat(userData.body()?.creditReportInfo?.score, `is`(362))
            MatcherAssert.assertThat(userData.body()?.creditReportInfo?.minScoreValue, `is`(0))
            MatcherAssert.assertThat(userData.body()?.creditReportInfo?.maxScoreValue, `is`(800))
        }

        mockWebServer.shutdown()
    }

    private fun createRepository(mockWebServer: MockWebServer) : CreditRepository {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CreditService::class.java)

        return CreditRepository(api)
    }

    private fun createServer(apiResponse: String, code: Int) : MockWebServer = MockWebServer().apply() {
        val text = javaClass.getResource("/api-response/${apiResponse}").readText()
        text?.let {
            enqueue(MockResponse()
                .setResponseCode(code)
                .setBody(text))
        }
    }
}