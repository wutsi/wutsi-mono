package com.wutsi.blog.pin.dto

data class StoryPinedEvent(
    val storyId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
