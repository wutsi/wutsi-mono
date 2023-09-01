package com.wutsi.platform.core.tracing

import jakarta.servlet.http.HttpServletRequest

interface TenantIdProvider {
    fun get(request: HttpServletRequest): String?
}
