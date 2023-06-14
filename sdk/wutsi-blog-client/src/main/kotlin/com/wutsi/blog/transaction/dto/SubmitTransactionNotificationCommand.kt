package com.wutsi.blog.transaction.dto

data class SubmitTransactionNotificationCommand(
    val transactionId: String = "",
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
