package com.wutsi.tracking.manager.entity

data class CampaignEntity(
    val campaign: String = "",
    val accountId: String? = null,
    val deviceId: String? = null,
    val totalImpressions: Long = 0,
)
