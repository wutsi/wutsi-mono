package com.wutsi.membership.manager.dto

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class MemberSummary(
    public val id: Long = 0,
    public val pictureUrl: String? = null,
    public val name: String? = null,
    public val displayName: String = "",
    public val active: Boolean = false,
    public val language: String = "",
    public val country: String = "",
    public val cityId: Long? = null,
    public val categoryId: Long? = null,
    public val business: Boolean = false,
    public val superUser: Boolean = false,
)
