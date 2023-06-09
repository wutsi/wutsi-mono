package com.wutsi.platform.core.error.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.error.controller-advice.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
open class ErrorConfiguration(
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    open fun restControllerErrorHandler(): RestControllerErrorHandler =
        RestControllerErrorHandler(logger, objectMapper)
}
