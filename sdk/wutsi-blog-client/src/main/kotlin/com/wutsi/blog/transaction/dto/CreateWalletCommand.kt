package com.wutsi.blog.transaction.dto

data class CreateWalletCommand(
    val userId: Long = -1,
    val country: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
