package com.wutsi.blog.story.dto

data class StoryUpdatedEventPayload(
    val title: String? = null,
    val content: String? = null,
    val summary: String? = null,
    val topicId: Long? = null,
    val tags: List<String>? = null,
    val tagline: String? = null,
    val access: StoryAccess? = null,
)
