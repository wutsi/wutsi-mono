package com.wutsi.blog.like.dto

data class LikeStory(
    val storyId: Long = -1,
    val count: Long = 0,
    val liked: Boolean = false,
)
