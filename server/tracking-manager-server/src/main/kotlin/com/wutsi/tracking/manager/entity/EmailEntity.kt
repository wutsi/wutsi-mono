package com.wutsi.tracking.manager.entity

data class EmailEntity(
    val accountId: String = "",
    val productId: String = "",
    val totalReads: Long = 0,
)
