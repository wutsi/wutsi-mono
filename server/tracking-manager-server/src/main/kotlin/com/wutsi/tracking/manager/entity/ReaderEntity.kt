package com.wutsi.tracking.manager.entity

data class ReaderEntity(
    val accountId: String? = null,
    val deviceId: String? = null,
    val productId: String = "",
    val totalReads: Long = 0,
)
