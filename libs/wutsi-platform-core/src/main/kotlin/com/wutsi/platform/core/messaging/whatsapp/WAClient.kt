package com.wutsi.platform.core.messaging.whatsapp

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

open class WAClient(
    private val phoneId: String,
    private val accessToken: String,
    private val client: HttpClient,
) {
    companion object {
        const val VERSION = "v13.0"
        private val LOGGER = LoggerFactory.getLogger(WAClient::class.java)
    }

    private val objectMapper: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    open fun messages(payload: WAMessage): WAResponse {
        val payload = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload))
        val uri = "https://graph.facebook.com/$VERSION/$phoneId/messages"
        val request = HttpRequest.newBuilder()
            .uri(URI(uri))
            .headers(
                "Authorization",
                "Bearer $accessToken",
                "Content-Type",
                "application/json",
            )
            .POST(payload)
            .build()

        // Response
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        LOGGER.info("POST $uri ${response.statusCode()} $payload ${response.body()}")
        if (response.statusCode() / 100 != 2) {
            throw WAException(response.statusCode(), response.body())
        }

        // Return
        return objectMapper.readValue(response.body(), WAResponse::class.java)
    }
}
