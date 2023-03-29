package com.wutsi.checkout.manager.dto

import kotlin.String

public data class CreateDonationResponse(
    public val transactionId: String = "",
    public val status: String = "",
)
