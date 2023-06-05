package com.wutsi.blog.story.dto

import java.util.Date

data class StoryPublicationScheduledEventPayload(
    val title: String? = null,
    val summary: String? = null,
    val topicId: Long? = null,
    val tags: List<String>? = null,
    val tagline: String? = null,
    val access: StoryAccess? = null,
    val scheduledPublishDateTime: Date = Date(),
)
