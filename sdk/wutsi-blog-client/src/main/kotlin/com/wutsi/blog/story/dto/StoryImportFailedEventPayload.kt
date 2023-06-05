package com.wutsi.blog.story.dto

data class StoryImportFailedEventPayload(
    val url: String = "",
    val statusCode: Int? = null,
    val message: String? = null,
    val exceptionClass: String = "",
)
