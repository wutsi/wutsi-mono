package com.wutsi.platform.core.storage.spring

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.local.LocalStorageService
import com.wutsi.platform.core.storage.local.LocalStorageServlet
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.storage.type"],
    havingValue = "local",
    matchIfMissing = true,
)
open class LocalStorageConfiguration(
    @Value("\${server.port}") private val port: Int,
    @Value("\${wutsi.platform.storage.local.directory:\${user.home}/wutsi/storage}") private val directory: String,
    @Value("\${wutsi.platform.storage.local.servlet.path:/storage}") private val servletPath: String,
) {
    @Bean
    open fun storageService(): StorageService =
        LocalStorageService(directory, "http://localhost:$port$servletPath")

    @Bean
    open fun storageServlet(): ServletRegistrationBean<*> =
        ServletRegistrationBean(LocalStorageServlet(directory), "$servletPath/*")
}
