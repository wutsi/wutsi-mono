package com.wutsi.platform.core.tracing.spring

import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.feign.FeignTracingRequestInterceptor
import com.wutsi.platform.core.tracing.servlet.DeviceIdFilter
import com.wutsi.platform.core.tracing.servlet.DeviceIdProviderCookie
import com.wutsi.platform.core.tracing.servlet.DeviceIdProviderHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.Ordered
import javax.servlet.http.HttpServletRequest

@Configuration
open class TracingConfiguration(
    @Autowired private val context: ApplicationContext,
    @Value("\${wutsi.platform.tracing.client-id}") private val clientId: String,
    @Value("\${wutsi.platform.tracing.device-id-provider.type:header}") private val deviceIdProviderType: String,
    @Value("\${wutsi.platform.tracing.device-id-provider.cookie.name:_w_did}") private val cookieName: String,
) {
    @Bean
    @Primary
    open fun tracingContext(): TracingContext =
        DynamicTracingContext(context)

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    open fun requestTracingContext(request: HttpServletRequest): RequestTracingContext =
        RequestTracingContext(request, deviceIdProvider())

    @Bean
    open fun tracingRequestInterceptor(): FeignTracingRequestInterceptor =
        FeignTracingRequestInterceptor(clientId, tracingContext())

    @Bean
    open fun deviceIdProvider(): DeviceIdProvider =
        if (deviceIdProviderType == "cookie") {
            DeviceIdProviderCookie(cookieName)
        } else {
            DeviceIdProviderHeader()
        }

    @Bean
    open fun deviceIdFilter(): FilterRegistrationBean<DeviceIdFilter> {
        val filter = FilterRegistrationBean(DeviceIdFilter(deviceIdProvider()))
        filter.order = Ordered.LOWEST_PRECEDENCE - 100
        return filter
    }
}
