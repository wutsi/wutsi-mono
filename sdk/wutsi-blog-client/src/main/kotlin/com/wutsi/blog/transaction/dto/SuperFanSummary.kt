package com.wutsi.blog.transaction.dto

data class SuperFanSummary(
    val id: String = "",
    val userId: Long = -1,
    val walletId: String = "",
    val transactionCount: Long = 0,
    val value: Long = 0,
)
