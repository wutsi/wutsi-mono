package com.wutsi.blog.story.dto

import java.util.Date

data class PublishStoryCommand(
    val storyId: Long = -1,
    val title: String? = null,
    val categoryId: Long? = null,
    val tagline: String? = null,
    val access: StoryAccess? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val scheduledPublishDateTime: Date? = null,
)
