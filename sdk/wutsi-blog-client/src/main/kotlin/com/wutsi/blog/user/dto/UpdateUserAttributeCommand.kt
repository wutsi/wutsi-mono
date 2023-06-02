package com.wutsi.blog.user.dto

import javax.validation.constraints.NotEmpty

data class UpdateUserAttributeCommand(
    val userId: Long = -1,
    @get:NotEmpty val name: String = "",
    val value: String? = null,
)
