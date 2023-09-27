package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.UrlShortener
import com.wutsi.platform.core.messaging.url.NullUrlShortener
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.url-shortener.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NoUrlShortenerConfiguration {
    @Bean
    open fun urlShortener(): UrlShortener =
        NullUrlShortener()
}
