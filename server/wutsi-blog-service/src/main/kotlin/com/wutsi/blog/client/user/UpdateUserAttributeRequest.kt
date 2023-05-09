package com.wutsi.blog.client.user

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UpdateUserAttributeRequest(
    @get:NotNull @get:NotEmpty val name: String? = null,
    val value: String? = null,
)
