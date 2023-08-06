package com.wutsi.blog.story.dto

import javax.validation.constraints.NotEmpty

data class RecommendStoryRequest(
    val blogId: Long? = null,
    val userId: Long? = null,
    @NotEmpty() val deviceId: String = "",
    val limit: Int = 20,
)
