package com.wutsi.blog.ads.dto

import javax.validation.constraints.NotEmpty

data class PublishAdsCommand(
    @get:NotEmpty val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
