package com.wutsi.marketplace.manager.event

data class CreateFundraisingEventPayload(
    val accountId: Long = -1,
    val fundraisingId: Long = -1,
)
