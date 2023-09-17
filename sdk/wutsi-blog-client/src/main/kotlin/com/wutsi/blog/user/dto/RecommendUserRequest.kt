package com.wutsi.blog.story.dto

import javax.validation.constraints.NotEmpty

data class RecommendStoryRequest(
    val readerId: Long? = null,
    @NotEmpty val deviceId: String = "",
    val limit: Int = 20,
)
