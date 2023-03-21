package com.wutsi.membership.access.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class SaveAccountDeviceRequest(
    @get:NotBlank
    public val token: String = "",
    public val type: String? = null,
    public val osName: String? = null,
    public val osVersion: String? = null,
    public val model: String? = null,
)
