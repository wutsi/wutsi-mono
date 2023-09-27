package com.wutsi.blog.config

import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.platform.core.storage.local.LocalStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.tracking.type"],
    havingValue = "local",
    matchIfMissing = true,
)
open class LocalTrackingConfiguration(
    @Value("\${server.port}") private val port: Int,
    @Value("\${wutsi.application.tracking.local.directory:\${user.home}/wutsi/storage/tracking-manager}") private val directory: String,
    @Value("\${wutsi.platform.storage.local.servlet.path:/storage}") private val servletPath: String,
) {
    @Bean
    fun trackingStorage(): TrackingStorageService =
        TrackingStorageService(
            LocalStorageService(directory, "http://localhost:$port$servletPath"),
        )
}
