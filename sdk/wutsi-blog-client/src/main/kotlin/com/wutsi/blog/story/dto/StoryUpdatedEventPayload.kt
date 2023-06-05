package com.wutsi.blog.story.dto

data class StoryUpdatedEventPayload(
    val title: String? = null,
    val content: String? = null,
)
