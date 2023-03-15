package com.wutsi.platform.core.security

import java.security.Key

interface KeyProvider {
    fun getKey(keyId: String): Key
}
