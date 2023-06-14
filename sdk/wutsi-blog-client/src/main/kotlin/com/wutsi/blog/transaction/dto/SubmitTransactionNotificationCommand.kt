package com.wutsi.blog.transaction.dto

data class ProcessTransactionNotificationCommand(
    val transactionId: String = "",
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
