package com.wutsi.blog.story.dto

data class StoryImportFailedEventPayload(
    val userId: Long = -1,
    val url: String = "",
    val statusCode: Int? = null,
    val message: String? = null,
    val exceptionClass: String = "",
)
