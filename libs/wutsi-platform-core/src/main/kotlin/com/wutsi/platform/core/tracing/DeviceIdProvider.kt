package com.wutsi.platform.core.tracing

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface DeviceIdProvider {
    fun get(request: HttpServletRequest): String?
    fun set(duid: String, request: HttpServletRequest, response: HttpServletResponse)
}
