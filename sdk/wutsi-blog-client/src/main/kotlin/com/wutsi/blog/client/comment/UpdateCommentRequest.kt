package com.wutsi.blog.client.comment

import javax.validation.constraints.NotBlank

data class UpdateCommentRequest(
    @get:NotBlank val text: String = "",
)
