package com.wutsi.platform.core.messaging.url

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.messaging.UrlShortener
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class BitlyUrlShortener(
    private val accessToken: String,
    private val objectMapper: ObjectMapper,
    private val client: HttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build(),
) : UrlShortener {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(BitlyUrlShortener::class.java)
        private val ENDPOINT = "https://api-ssl.bitly.com/v4/shorten"
    }

    override fun shorten(url: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI(ENDPOINT))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $accessToken")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "long_url": "$url"
                    }
                    """.trimIndent(),
                ),
            )
            .build()

        try {
            val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() / 100 == 2) {
                val data = objectMapper.readValue(response.body(), Map::class.java)
                return data["link"]?.toString() ?: url
            } else {
                LOGGER.warn("Unable to shorten $url. Error=${response.statusCode()} - ${response.body()}")
                return url
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to shorten $url.", ex)
            return url
        }
    }
}
