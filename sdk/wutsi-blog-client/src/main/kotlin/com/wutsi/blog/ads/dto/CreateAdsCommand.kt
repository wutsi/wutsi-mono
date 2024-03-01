package com.wutsi.blog.ads.dto

data class CreateAdsCommand(
    val userId: Long = -1,
    val title: String = "",
    val type: AdsType = AdsType.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis()
)
