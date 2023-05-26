package com.wutsi.blog.like.dto

object LikeEventType {
    const val LIKE_STORY_COMMAND = "urn:wutsi:command:like-story"
    const val UNLIKE_STORY_COMMAND = "urn:wutsi:command:unlike-story"

    const val STORY_LIKED_EVENT = "urn:wutsi:event:story-liked"
    const val STORY_UNLIKED_EVENT = "urn:wutsi:event:story-unliked"
}
