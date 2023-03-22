package com.wutsi.membership.manager.dto

import kotlin.String

public data class UpdateMemberAttributeRequest(
    public val name: String = "",
    public val `value`: String? = null,
)
