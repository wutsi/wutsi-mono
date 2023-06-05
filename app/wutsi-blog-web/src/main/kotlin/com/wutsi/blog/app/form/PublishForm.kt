package com.wutsi.blog.app.form

import com.wutsi.blog.story.dto.StoryAccess

data class PublishForm(
    val id: Long = -1,
    val title: String = "",
    val tagline: String = "",
    val summary: String = "",
    val topicId: String = "",
    val tags: List<String> = emptyList(),
    val publishNow: Boolean = true,
    val scheduledPublishDate: String = "",
    val access: StoryAccess = StoryAccess.PUBLIC,
)
