package com.wutsi.platform.core.tracing.servlet

import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

open class DeviceIdProviderHeader : DeviceIdProvider {
    override fun get(request: HttpServletRequest): String? =
        request.getHeader(TracingContext.HEADER_DEVICE_ID)

    override fun set(duid: String, request: HttpServletRequest, response: HttpServletResponse) {
        response.addHeader(TracingContext.HEADER_DEVICE_ID, duid)
    }
}
