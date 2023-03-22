package com.wutsi.marketplace.manager.dto

import kotlin.String

public data class UpdateStorePolicyAttributeRequest(
    public val name: String = "",
    public val `value`: String? = null,
)
