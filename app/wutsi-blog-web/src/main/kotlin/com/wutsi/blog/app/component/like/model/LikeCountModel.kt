package com.wutsi.blog.app.component.like.model

data class LikeCountModel(
    val storyId: Long = -1,
    val value: Long = 0,
    val valueText: String = "",
)
