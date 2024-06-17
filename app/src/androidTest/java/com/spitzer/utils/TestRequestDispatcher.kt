package com.spitzer.utils

import android.content.Context
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

typealias MockRequestHandler = (request: RecordedRequest) -> MockResponse

class UiTestRequestDispatcher(private val context: Context) : Dispatcher() {

    private val simpleResponses = mutableMapOf<String, MockResponse?>()
    private val complexResponses = mutableMapOf<String, MockRequestHandler>()

    fun addResponse(
        pathPattern: String,
        filename: String,
        httpMethod: String = "GET",
        status: Int = HttpsURLConnection.HTTP_OK
    ) {
        val body = TestFileUtils.readFile(context, filename)
        val response = mockResponse(body, status)
        val responseKey = "$httpMethod$pathPattern"
        // adding the http method into the key allows for a repeated pathPattern
        // that is used by both GET and POST to behave differently for eg.
        if (simpleResponses[responseKey] != null) {
            simpleResponses.replace(responseKey, response)
        } else {
            simpleResponses[responseKey] = response
        }
    }

    fun addResponse(
        pathPattern: String,
        requestHandler: MockRequestHandler,
        httpMethod: String = "GET",
    ) {
        val responseKey = "$httpMethod$pathPattern"
        if (complexResponses[responseKey] != null) {
            complexResponses.replace(responseKey, requestHandler)
        } else {
            complexResponses[responseKey] = requestHandler
        }
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        println("Incoming request: $request")
        Thread.sleep(200) // provide a small delay to better mimic real life network call across a mobile network
        val responseKey = "${request.method}${request.path}"
        var response = getSimpleResponse(responseKey)
        if (response == null) {
            response = errorResponse(responseKey)
        }
        return response
    }

    private fun getSimpleResponse(responseKey: String): MockResponse? {
        return simpleResponses[responseKey]
    }

    private fun errorResponse(reason: String): MockResponse {
        return mockResponse(
            """{"error":"response not found for "$reason"}""",
            HttpURLConnection.HTTP_INTERNAL_ERROR
        )
    }

    fun removeResponse(
        pathPattern: String,
        httpMethod: String = "GET",
    ) {
        val responseKey = "$httpMethod$pathPattern"
        simpleResponses[responseKey] = null
    }
}
