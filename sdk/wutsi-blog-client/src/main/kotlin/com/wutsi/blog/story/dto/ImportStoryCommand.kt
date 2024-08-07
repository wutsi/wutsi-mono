package com.wutsi.blog.story.dto

import jakarta.validation.constraints.NotEmpty

data class ImportStoryCommand(
    @get:NotEmpty val url: String = "",
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
