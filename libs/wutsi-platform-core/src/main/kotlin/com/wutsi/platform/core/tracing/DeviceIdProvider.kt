package com.wutsi.platform.core.tracing

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface DeviceIdProvider {
    fun get(request: HttpServletRequest): String?
    fun set(duid: String, request: HttpServletRequest, response: HttpServletResponse)
}
