package com.wutsi.platform.core.security.spring

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

open class AbstractWebSecurityConfiguration {
    @Bean
    @ConditionalOnProperty(
        value = ["wutsi.platform.security.cors.enabled"],
        havingValue = "true",
        matchIfMissing = true,
    )
    open fun corsConfiguration(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "OPTIONS", "HEAD", "PUT", "POST", "DELETE")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
