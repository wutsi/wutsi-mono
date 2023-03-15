package com.wutsi.platform.core.messaging

interface UrlShortener {
    fun shorten(url: String): String
}
