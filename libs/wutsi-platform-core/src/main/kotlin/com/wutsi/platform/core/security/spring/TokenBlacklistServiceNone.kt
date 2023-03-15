package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenBlacklistService

class TokenBlacklistServiceNone : TokenBlacklistService {
    override fun add(token: String, ttl: Long) {}
    override fun contains(token: String): Boolean =
        false
}
