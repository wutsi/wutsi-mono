package com.wutsi.platform.core.tracing.servlet

import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID

class HttpTracingContext {
    fun traceId(request: HttpServletRequest): String {
        var traceId = request.getAttribute(TracingContext.HEADER_TRACE_ID)
        if (traceId == null) {
            traceId = request.getHeader(TracingContext.HEADER_TRACE_ID)
                ?: request.getHeader(TracingContext.HEADER_HEROKU_REQUEST_ID) ?: UUID.randomUUID().toString()
        }
        request.setAttribute(TracingContext.HEADER_TRACE_ID, traceId)

        return traceId.toString()
    }

    fun clientId(request: HttpServletRequest): String? =
        request.getHeader(TracingContext.HEADER_CLIENT_ID)

    fun deviceId(request: HttpServletRequest, deviceIdProvider: DeviceIdProvider): String? =
        deviceIdProvider.get(request)

    fun tenantId(request: HttpServletRequest): String? =
        request.getHeader(TracingContext.HEADER_TENANT_ID)

    fun clientInfo(request: HttpServletRequest): String? =
        request.getHeader(TracingContext.HEADER_CLIENT_INFO)
}
