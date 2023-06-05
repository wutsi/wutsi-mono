package com.wutsi.blog.story.dto

data class StoryCreatedEventPayload(
    val title: String? = null,
    val content: String? = null,
)
