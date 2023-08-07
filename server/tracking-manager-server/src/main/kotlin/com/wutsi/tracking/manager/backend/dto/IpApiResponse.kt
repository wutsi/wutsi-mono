package com.wutsi.blog.app.backend.dto

data class IpApiResponse(
    val country: String = "",
    val countryCode: String = "",
    val status: String = "",
    val city: String = "",
    val zip: String = "",
    val lat: Double = -1.0,
    val long: Double = -1.0,
    val isp: String = "",
    val region: String = "",
    val regionName: String = "",
)
