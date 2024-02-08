package com.wutsi.platform.payment.core

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpRequest.Builder
import java.net.http.HttpResponse

open class Http(
    private val client: HttpClient,
    private val objectMapper: ObjectMapper,
    private val listener: HttpListener = DefaultHttpListener(),
) {
    open fun <T> get(
        referenceId: String,
        uri: String,
        responseType: Class<T>,
        headers: Map<String, String?> = emptyMap(),
    ): T? {
        val request = HttpRequest.newBuilder()
            .uri(URI(uri))
            .headers(headers)
            .GET()
            .build()

        return handle(referenceId, responseType, request, null)
    }

    open fun <T> post(
        referenceId: String,
        uri: String,
        requestPayload: Any,
        responseType: Class<T>,
        headers: Map<String, String?> = emptyMap(),
    ): T? {
        val requestBody = if (headers["Content-Type"] == "application/json") {
            objectMapper.writeValueAsString(requestPayload)
        } else {
            requestPayload.toString()
        }
        val request = HttpRequest.newBuilder()
            .uri(URI(uri))
            .headers(headers)
            .POST(BodyPublishers.ofString(requestBody))
            .build()

        return handle(referenceId, responseType, request, requestBody)
    }

    private fun <T> handle(
        referenceId: String,
        responseType: Class<T>,
        request: HttpRequest,
        requestBody: String?,
    ): T? {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        try {
            if (response.statusCode() / 100 == 2) {
                return if (response.body().isEmpty()) {
                    null
                } else {
                    objectMapper.readValue(response.body(), responseType)
                }
            } else {
                throw HttpException(response.statusCode(), response.body())
            }
        } finally {
            notify(referenceId, request, requestBody, response)
        }
    }

    private fun Builder.headers(headers: Map<String, String?>): Builder {
        headers.keys.forEach {
            if (headers[it] != null) {
                this.header(it, headers[it])
            }
        }
        return this
    }

    private fun notify(
        referenceId: String,
        request: HttpRequest,
        requestBody: String?,
        response: HttpResponse<String>,
    ) {
        listener.notify(
            referenceId,
            request.method(),
            request.uri().toString(),
            response.statusCode(),
            requestBody,
            response.body(),
        )
    }
}
