package com.wutsi.blog.ads.dto

import javax.validation.constraints.NotEmpty

data class UpdateAdsAttributeCommand(
    @get:NotEmpty val adsId: String = "",
    @get:NotEmpty val name: String = "",
    val value: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
