package com.wutsi.blog.story.dto

import javax.validation.constraints.NotEmpty

data class RecommendStoryRequest(
    val blogId: Long = -1,
    val userId: Long? = null,
    @NotEmpty val deviceId: String = "",
    val limit: Int = 20,
)
