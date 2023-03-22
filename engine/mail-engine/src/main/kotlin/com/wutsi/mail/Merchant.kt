package com.wutsi.mail

data class Merchant(
    val name: String,
    val country: String,
    val url: String,
    val logoUrl: String?,
    val category: String?,
    val location: String?,
    val phoneNumber: String,
    val whatsapp: Boolean,
    val facebookId: String?,
    val instagramId: String?,
    val twitterId: String?,
    val youtubeId: String?,
    val websiteUrl: String?,
)
