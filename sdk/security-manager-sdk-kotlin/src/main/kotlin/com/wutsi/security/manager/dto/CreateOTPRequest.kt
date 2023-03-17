package com.wutsi.security.manager.dto

import kotlin.String

public data class CreateOTPRequest(
    public val address: String = "",
    public val type: String = "",
)
