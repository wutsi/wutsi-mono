package com.wutsi.blog.app.config

import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.spring.SpringDebugRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestConfiguration(
    private val tracingContext: TracingContext,
    private val tokenProvider: TokenProvider,
    @Value("\${wutsi.application.backend.connection-timeout}") private val connectionTimeout: Long,
    @Value("\${wutsi.application.backend.read-timeout}") private val readTimeout: Long,
) {
    @Bean
    fun restTemplate(): RestTemplate =
        RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(connectionTimeout))
            .setReadTimeout(Duration.ofMillis(readTimeout))
            .interceptors(
                SpringDebugRequestInterceptor(),
                SpringTracingRequestInterceptor(tracingContext),
                SpringAuthorizationRequestInterceptor(tokenProvider),
            )
            .build()
}
