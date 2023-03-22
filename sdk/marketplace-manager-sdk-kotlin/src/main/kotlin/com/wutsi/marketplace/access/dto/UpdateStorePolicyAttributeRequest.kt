package com.wutsi.marketplace.access.dto

import kotlin.String

public data class UpdateStorePolicyAttributeRequest(
    public val name: String = "",
    public val `value`: String? = null,
)
