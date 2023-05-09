package com.wutsi.blog.client.story

data class TopicDto(
    val id: Long = -1,
    val parentId: Long = -1,
    val name: String = "",
)
