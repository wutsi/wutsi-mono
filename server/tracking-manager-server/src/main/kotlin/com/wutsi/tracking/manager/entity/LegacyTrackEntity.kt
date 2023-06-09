package com.wutsi.tracking.manager.entity

data class LegacyTrackEntity(
    val time: Long? = null,
    val deviceId: String? = null,
    val userId: String? = null,
    val userAgent: String? = null,
    val ip: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,

    val referer: String? = null,
    val page: String? = null,
    val event: String? = null,
    val productId: String? = null,
    val value: String? = null,

    var os: String? = null,
    var browser: String? = null,
    var device: String? = null,
    var bot: Boolean = false,
    var trafficType: String? = null,
    var hitId: String? = null,

    var url: String? = null,
    var source: String? = null,
    var campaign: String? = null,
    var medium: String? = null,
    var siteid: String? = null,
    var impressions: String? = null,
)
