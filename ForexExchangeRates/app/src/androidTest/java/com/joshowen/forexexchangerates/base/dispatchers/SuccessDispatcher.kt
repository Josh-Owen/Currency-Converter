package com.joshowen.forexexchangerates.base.dispatchers

import android.content.Context
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.joshowen.forexexchangerates.base.FileReader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class SuccessDispatcher(
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
) : Dispatcher() {
    private val responseFilesByPath: Map<String, String> = mapOf(
        "latest" to "network_files/FetchCurrentPriceResponse.json",
        "timeseries" to "network_files/PriceHistoryResponse.json"
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        val errorResponse = MockResponse().setResponseCode(404)

        val pathWithoutQueryParams = Uri.parse(request.path).path ?: return errorResponse
        val responseFile = responseFilesByPath[pathWithoutQueryParams]

        return if (responseFile != null) {
            val responseBody = FileReader.readStringFromFile(responseFile)
            MockResponse().setResponseCode(200).setBody(responseBody)
        } else {
            errorResponse
        }
    }
}