package com.wutsi.platform.payment.core

data class Error(
    val code: ErrorCode = ErrorCode.NONE,
    val transactionId: String = "",
    val supplierErrorCode: String? = null,
    val message: String? = null,
    val errorId: String? = null,
)
