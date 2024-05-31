package com.wutsi.blog.product.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LiretamaService(
    @Value("\${wutsi.application.liretama.affiliate-id}") private val affiliateId: String,
) {
    companion object {
        private const val PRODUCT_URL_PREFIX = "https://www.liretama.com/livres/"
    }

    fun isValidProductURL(url: String?): Boolean =
        url.isNullOrEmpty() || url.lowercase().startsWith(PRODUCT_URL_PREFIX)

    fun isLiretamaProductURL(url: String): Boolean =
        url.lowercase().startsWith("https://www.liretama.com/livres/")

    fun toProductUrl(url: String): String =
        if (isLiretamaProductURL(url)) {
            sanitizeUrl(url) + "?pid=$affiliateId"
        } else {
            url
        }

    private fun sanitizeUrl(url: String): String {
        val i = url.indexOf("?")
        return if (i > 0) {
            url.substring(0, i)
        } else {
            url
        }
    }
}
