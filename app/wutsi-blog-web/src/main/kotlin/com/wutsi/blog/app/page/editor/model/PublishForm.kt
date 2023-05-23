package com.wutsi.blog.app.page.editor.model

import com.wutsi.blog.client.story.StoryAccess

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
