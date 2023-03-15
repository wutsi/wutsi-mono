package com.wutsi.platform.core.logging.spring

import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.logging.servlet.KVLoggerFilter
import com.wutsi.platform.core.tracing.DeviceIdProvider
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.Ordered

@Configuration
open class LoggingConfiguration(
    private val context: ApplicationContext,
    private val deviceIdProvider: DeviceIdProvider,
) {
    @Bean
    open fun loggingFilter(): FilterRegistrationBean<KVLoggerFilter> {
        val filter = FilterRegistrationBean(KVLoggerFilter(logger(), deviceIdProvider))
        filter.order = Ordered.LOWEST_PRECEDENCE
        return filter
    }

    @Bean
    open fun logger(): KVLogger =
        DynamicKVLogger(context)

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    open fun requestLogger(): DefaultKVLogger =
        DefaultKVLogger()
}
