package com.wutsi.checkout.manager.dto

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Business(
    public val id: Long = 0,
    public val accountId: Long = 0,
    public val balance: Long = 0,
    public val cashoutBalance: Long = 0,
    public val country: String = "",
    public val currency: String = "",
    public val status: String = "",
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    public val deactivated: OffsetDateTime? = null,
    public val totalOrders: Long = 0,
    public val totalSales: Long = 0,
    public val totalViews: Long = 0,
    public val totalDonations: Long = 0,
    public val totalDonationValue: Long = 0,
)
