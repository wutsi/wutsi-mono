package com.wutsi.platform.core.messaging.url

import com.wutsi.platform.core.messaging.UrlShortener

class NullUrlShortener : UrlShortener {
    override fun shorten(url: String) = url
}
