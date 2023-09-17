package com.wutsi.blog.user.dto

import javax.validation.constraints.NotEmpty

data class RecommendUserRequest(
    val readerId: Long? = null,
    @NotEmpty val deviceId: String = "",
    val limit: Int = 20,
)
