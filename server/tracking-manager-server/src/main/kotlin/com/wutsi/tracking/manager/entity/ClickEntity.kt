package com.wutsi.tracking.manager.entity

data class ClickEntity(
    val productId: String = "",
    val accountId: String? = null,
    val deviceId: String? = null,
    val totalClicks: Long = 0,
)
