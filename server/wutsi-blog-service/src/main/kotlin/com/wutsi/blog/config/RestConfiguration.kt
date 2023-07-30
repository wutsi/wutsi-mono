package com.wutsi.blog.config

import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.spring.SpringDebugRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfiguration(
    private val tracingContext: TracingContext,
    private val tokenProvider: TokenProvider,
) {
    @Bean
    fun restTemplate(): RestTemplate {
        val rest = RestTemplate()
        rest.interceptors = listOf(
            SpringDebugRequestInterceptor(),
            SpringTracingRequestInterceptor(tracingContext),
            SpringAuthorizationRequestInterceptor(tokenProvider),
        )
        return rest
    }
}
