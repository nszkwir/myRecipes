package com.spitzer.utils

import com.spitzer.data.utils.TestUrlConfiguration.MOCK_SERVER_PORT
import okhttp3.mockwebserver.MockWebServer
import org.junit.AfterClass
import org.junit.BeforeClass
import java.net.HttpURLConnection

open class BaseUiTest {

    companion object {
        protected val dispatcher = UiTestRequestDispatcher(UiTestUtils.testContext)
        protected var webServer: MockWebServer? = null

        @BeforeClass
        @JvmStatic
        fun startMockServer() {
            if (webServer == null) {
                println("Mock Web Server starting")
                webServer = MockWebServer()
                webServer!!.start(MOCK_SERVER_PORT)
                webServer!!.dispatcher = dispatcher
            }
        }

        @AfterClass
        @JvmStatic
        fun shutDownServer() {
            webServer?.shutdown()
            webServer = null
        }
    }

    fun addResponse(
        pathPattern: String,
        filename: String,
        httpMethod: String = "GET",
        status: Int = HttpURLConnection.HTTP_OK
    ) = dispatcher.addResponse(pathPattern, filename, httpMethod, status)

    fun addResponse(
        pathPattern: String,
        requestHandler: MockRequestHandler,
        httpMethod: String = "GET",
    ) = dispatcher.addResponse(pathPattern, requestHandler, httpMethod)

    fun removeResponse(
        pathPattern: String,
        httpMethod: String = "GET",
    ) = dispatcher.removeResponse(pathPattern, httpMethod)
}
