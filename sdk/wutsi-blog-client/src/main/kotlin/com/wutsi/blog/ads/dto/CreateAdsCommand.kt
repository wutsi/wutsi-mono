package com.wutsi.blog.ads.dto

import javax.validation.constraints.NotBlank

data class CreateAdsCommand(
    val userId: Long = -1,
    @get:NotBlank val title: String = "",
    val type: AdsType = AdsType.UNKNOWN,
    @get:NotBlank val currency: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
