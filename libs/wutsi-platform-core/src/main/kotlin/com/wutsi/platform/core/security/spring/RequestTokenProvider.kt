package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenProvider
import javax.servlet.http.HttpServletRequest

open class RequestTokenProvider(
    private val request: HttpServletRequest,
) : TokenProvider {
    override fun getToken(): String? {
        val value = request.getHeader("Authorization") ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }
}
