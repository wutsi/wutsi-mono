package com.wutsi.blog.story.dto

data class StoryPublishedEventPayload(
    val title: String? = null,
    val tagline: String? = null,
    val access: StoryAccess? = null,
    val categoryId: Long? = null,
)
