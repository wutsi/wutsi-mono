package com.wutsi.blog.app.page.story.model

data class TopicModel(
    val id: Long = -1,
    val parentId: Long = -1,
    val name: String = "",
    val displayName: String = "",
)
