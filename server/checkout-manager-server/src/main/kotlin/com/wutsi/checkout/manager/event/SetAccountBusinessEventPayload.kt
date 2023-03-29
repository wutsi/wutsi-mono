package com.wutsi.checkout.manager.event

data class SetAccountBusinessEventPayload(
    val accountId: Long = -1,
    val businessId: Long = -1,
)
