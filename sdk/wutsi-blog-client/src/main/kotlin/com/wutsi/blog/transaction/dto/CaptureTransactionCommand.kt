package com.wutsi.blog.transaction.dto

import jakarta.validation.constraints.NotEmpty

data class CaptureTransactionCommand(
    @NotEmpty val gatewayTransactionId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
