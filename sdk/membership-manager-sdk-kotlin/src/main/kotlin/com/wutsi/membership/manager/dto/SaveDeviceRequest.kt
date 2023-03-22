package com.wutsi.membership.manager.dto

import kotlin.String

public data class SaveDeviceRequest(
    public val token: String = "",
    public val type: String? = null,
    public val osName: String? = null,
    public val osVersion: String? = null,
    public val model: String? = null,
)
