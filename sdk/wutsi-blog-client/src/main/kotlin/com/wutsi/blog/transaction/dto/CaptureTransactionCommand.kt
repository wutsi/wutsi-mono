package com.wutsi.blog.transaction.dto

import jakarta.validation.constraints.NotEmpty

data class CaptureTransactionCommand(
    @NotEmpty val transactionId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
