package com.wutsi.marketplace.manager.event

data class CreateStoreEventPayload(
    val accountId: Long = -1,
    val storeId: Long = -1,
)
