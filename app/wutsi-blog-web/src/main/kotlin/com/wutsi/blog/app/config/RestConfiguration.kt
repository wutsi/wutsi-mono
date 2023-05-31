package com.wutsi.blog.app.config

import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.spring.SpringDebugRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfiguration(
    private val tracingContext: TracingContext,
) {
    @Bean
    fun restTemplate(): RestTemplate {
        val rest = RestTemplate()
        rest.interceptors = listOf(
            SpringDebugRequestInterceptor(),
            SpringTracingRequestInterceptor(tracingContext),
        )
        return rest
    }
}
