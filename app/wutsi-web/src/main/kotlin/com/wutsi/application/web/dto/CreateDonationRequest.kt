package com.wutsi.application.web.dto

data class CreateDonationRequest(
    val email: String = "",
    val displayName: String = "",
    val notes: String = "",
    val businessId: Long,
)
