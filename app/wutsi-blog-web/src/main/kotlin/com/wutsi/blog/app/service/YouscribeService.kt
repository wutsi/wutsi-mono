package com.wutsi.blog.app.service

import org.springframework.stereotype.Component

@Component
class YouscribeService {
    fun accept(url: String): Boolean =
        url.lowercase().startsWith("https://www.youscribe.com/")
}
