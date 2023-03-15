package com.wutsi.platform.core.security

interface TokenBlacklistService {
    fun add(token: String, ttl: Long)
    fun contains(token: String): Boolean
}
