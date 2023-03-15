package com.wutsi.platform.core.security

interface TokenProvider {
    fun getToken(): String?
}
