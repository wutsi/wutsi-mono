package com.wutsi.membership.manager.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class SearchMemberRequest(
    public val phoneNumber: String? = null,
    public val business: Boolean? = null,
    public val store: Boolean? = null,
    public val cityId: Long? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
