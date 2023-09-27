package com.wutsi.platform.core.messaging.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.messaging.UrlShortener
import com.wutsi.platform.core.messaging.url.BitlyUrlShortener
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.url-shortener.type"],
    havingValue = "bitly",
)
open class BitlyUrlShortenerConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${wutsi.platform.messaging.url-shortener.bitly.access-token}") private val accessToken: String,
) {
    @Bean
    open fun urlShortener(): UrlShortener =
        BitlyUrlShortener(accessToken, objectMapper)
}
