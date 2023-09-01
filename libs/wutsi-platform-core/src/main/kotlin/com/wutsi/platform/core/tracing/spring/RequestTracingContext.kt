package com.wutsi.platform.core.tracing.spring

import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.servlet.HttpTracingContext
import jakarta.servlet.http.HttpServletRequest

open class RequestTracingContext(
    private val request: HttpServletRequest,
    private val deviceIdProvider: DeviceIdProvider,
) : TracingContext {
    private val delegate = HttpTracingContext()

    override fun traceId(): String = delegate.traceId(request)

    override fun clientId() = delegate.clientId(request) ?: TracingContext.NONE
    override fun deviceId() = delegate.deviceId(request, deviceIdProvider) ?: TracingContext.NONE
    override fun tenantId() = delegate.tenantId(request)
    override fun clientInfo() = delegate.clientInfo(request)
}
