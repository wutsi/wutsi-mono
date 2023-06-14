package com.wutsi.blog.transaction.dto

data class SubmitDonationResponse(
    val transactionId: String = "",
    val status: String = "",
    val errorCode: String? = null,
    val errorMessage: String? = null,
)
