package com.wutsi.blog.ads.dto

data class AdsImpressionContext(
    val blogId: Long? = null,
    val userId: Long? = null,
    val ip: String? = null,
    val adsPerType: Int = 1,
    val userAgent: String? = null,
)
