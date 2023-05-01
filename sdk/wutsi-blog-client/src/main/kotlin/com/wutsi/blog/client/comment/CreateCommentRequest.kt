package com.wutsi.blog.client.comment

import javax.validation.constraints.NotBlank

data class CreateCommentRequest(
    val userId: Long? = null,
    val storyId: Long? = null,
    @get:NotBlank val text: String = "",
)
