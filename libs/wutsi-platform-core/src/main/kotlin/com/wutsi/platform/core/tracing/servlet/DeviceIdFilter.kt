package com.wutsi.platform.core.tracing.servlet

import com.wutsi.platform.core.tracing.DeviceIdProvider
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class DeviceIdFilter(private val deviceIdProvider: DeviceIdProvider) : Filter {
    override fun destroy() {
    }

    override fun init(config: FilterConfig?) {
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            val value = deviceIdProvider.get(request as HttpServletRequest)
            if (value != null) {
                deviceIdProvider.set(value, request, response as HttpServletResponse)
            }
        } finally {
            chain.doFilter(request, response)
        }
    }
}
