package com.wutsi.platform.core.logging.servlet

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.servlet.HttpTracingContext
import org.springframework.http.HttpHeaders
import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class KVLoggerFilter(
    private val kv: KVLogger,
    private val deviceIdProvider: DeviceIdProvider,
) : Filter {
    private val tracingContext = HttpTracingContext()

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        // Empty
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val startTime = System.currentTimeMillis()
        try {
            filterChain.doFilter(servletRequest, servletResponse)
            log(startTime, (servletResponse as HttpServletResponse).status, servletRequest as HttpServletRequest, kv)
            kv.log()
        } catch (e: Exception) {
            log(startTime, 500, servletRequest as HttpServletRequest, kv)
            kv.setException(e)
            kv.log()
            throw e
        }
    }

    override fun destroy() {
        // Empty
    }

    private fun log(
        startTime: Long,
        status: Int,
        request: HttpServletRequest,
        kv: KVLogger,
    ) {
        val latencyMillis = System.currentTimeMillis() - startTime

        kv.add("success", status / 100 == 2)
        kv.add("latency_millis", latencyMillis)

        kv.add("http_status", status.toLong())
        kv.add("http_endpoint", request.requestURI)
        kv.add("http_method", request.method)
        kv.add("http_referer", request.getHeader(HttpHeaders.REFERER))
        kv.add("http_user_agent", request.getHeader(HttpHeaders.USER_AGENT))
        kv.add("trace_id", tracingContext.traceId(request))
        kv.add("client_id", tracingContext.clientId(request))
        kv.add("device_id", tracingContext.deviceId(request, deviceIdProvider))
        kv.add("tenant_id", tracingContext.tenantId(request))
        kv.add("client_info", tracingContext.clientInfo(request))

        val params = request.parameterMap
        params.keys.forEach { kv.add("http_param_$it", params[it]?.toList()) }

        request.getHeader("Authorization")?.let { kv.add("http_authorization", "***") }
        request.getHeader("X-Api-Key")?.let { kv.add("api_key", "***") }
        request.getHeader("Accept-Language")?.let { kv.add("language", it) }
    }
}
