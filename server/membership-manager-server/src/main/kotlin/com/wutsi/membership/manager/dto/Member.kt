package com.wutsi.membership.manager.dto

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class Member(
    public val id: Long = 0,
    public val name: String? = null,
    public val phoneNumber: String = "",
    public val email: String? = null,
    public val pictureUrl: String? = null,
    public val displayName: String = "",
    public val active: Boolean = false,
    public val language: String = "",
    public val country: String = "",
    public val business: Boolean = false,
    public val superUser: Boolean = false,
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
    public val fundraisingId: Long? = null,
    public val businessId: Long? = null,
    public val city: Place? = null,
    public val category: Category? = null,
)
