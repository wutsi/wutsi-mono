package com.wutsi.blog.product.service

import org.springframework.stereotype.Component

@Component
class LiretamaService {
    companion object {
        private const val PRODUCT_URL_PREFIX = "https://www.liretama.com/livres/"
    }

    fun isValidProductURL(url: String?): Boolean =
        url.isNullOrEmpty() || url.lowercase().startsWith(PRODUCT_URL_PREFIX)
}
