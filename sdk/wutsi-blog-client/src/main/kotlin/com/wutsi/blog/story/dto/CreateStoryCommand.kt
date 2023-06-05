package com.wutsi.blog.story.dto

import javax.validation.constraints.NotNull

data class CreateStoryCommand(
    @get:NotNull val userId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
