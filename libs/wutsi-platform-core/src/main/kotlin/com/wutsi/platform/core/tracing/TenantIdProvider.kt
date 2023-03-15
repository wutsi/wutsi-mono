package com.wutsi.platform.core.tracing

import javax.servlet.http.HttpServletRequest

interface TenantIdProvider {
    fun get(request: HttpServletRequest): String?
}
