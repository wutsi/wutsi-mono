package com.wutsi.blog.story.dto

import java.util.Date

data class PublishStoryCommand(
    val storyId: Long = -1,
    val title: String? = null,
    val summary: String? = null,
    val topicId: Long? = null,
    val categoryId: Long? = null,
    val tags: List<String>? = null,
    val tagline: String? = null,
    val access: StoryAccess? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val scheduledPublishDateTime: Date? = null,
)
