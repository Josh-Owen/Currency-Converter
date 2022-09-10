package com.joshowen.forexexchangerates.base.dispatchers

import android.net.Uri
import com.joshowen.forexexchangerates.base.utils.fileio.FileReader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class LoadHistoricPricesFailedDispatcher : Dispatcher() {

    private val responseFilesByPath: Map<String, String> = mapOf(
        "/latest" to "network_files/FetchCurrentPriceResponse200.json",
    )

    override fun dispatch(request: RecordedRequest): MockResponse {
        val errorResponse = MockResponse().setResponseCode(404)
        val pathWithoutQueryParams = Uri.parse(request.path).path ?: return errorResponse
        val responseFile = responseFilesByPath[pathWithoutQueryParams]

        return if (responseFile != null) {
            val responseBody = FileReader.readStringFromFile(responseFile)
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
        } else {
            errorResponse
        }
    }
}