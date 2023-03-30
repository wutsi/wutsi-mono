package com.wutsi.membership.access.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

public data class Account(
    public val id: Long = 0,
    public val name: String? = null,
    public val email: String? = null,
    public val phone: Phone = Phone(),
    public val city: Place? = null,
    public val category: Category? = null,
    public val pictureUrl: String? = null,
    public val status: String = "",
    public val displayName: String = "",
    public val language: String = "",
    public val country: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val deactivated: OffsetDateTime? = null,
    public val superUser: Boolean = false,
    public val business: Boolean = false,
    public val biography: String? = null,
    public val website: String? = null,
    public val whatsapp: Boolean = false,
    public val street: String? = null,
    public val timezoneId: String? = null,
    public val facebookId: String? = null,
    public val instagramId: String? = null,
    public val twitterId: String? = null,
    public val youtubeId: String? = null,
    public val storeId: Long? = null,
    var fundraisingId: Long? = null,
    public val businessId: Long? = null,
)
