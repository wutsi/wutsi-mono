package com.wutsi.blog.app.component.like.model

data class LikeModel(
    val id: Long = -1,
    val storyId: Long = -1,
    val userId: Long? = null
)
