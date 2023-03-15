package com.wutsi.platform.core.qrcode

interface KeyProvider {
    fun getKeyId(): String
    fun getKey(id: String): String
}
