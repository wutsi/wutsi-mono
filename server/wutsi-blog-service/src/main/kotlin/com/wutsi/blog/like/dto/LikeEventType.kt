package com.wutsi.blog.like.dto

object LikeEventType {
    const val LIKE_STORY = "urn:wutsi:command:like-story"
    const val UNLIKE_STORY = "urn:wutsi:command:unlike-story"

    const val STORY_LIKED = "urn:wutsi:event:story-liked"
    const val STORY_UNLIKED = "urn:wutsi:event:story-unliked"
}
