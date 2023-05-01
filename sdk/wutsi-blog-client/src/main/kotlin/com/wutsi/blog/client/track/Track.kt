package com.wutsi.blog.client.track

data class Track(
    val transactionId: String = "",
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

    var os: OS = OS(),
    var browser: Browser = Browser(),
    var device: Device = Device(),
    var bot: Boolean = false,
    var trafficType: TrafficType = TrafficType.unknown,
    var hitId: String? = null,

    var url: String? = null,
    var source: String? = null,
    var campaign: String? = null,
    var medium: String? = null,
)
