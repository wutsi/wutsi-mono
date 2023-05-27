package com.wutsi.blog.event

object EventType {
    // Command
    const val COMMENT_STORY_COMMAND = "urn:wutsi:command:comment-story"

    const val LIKE_STORY_COMMAND = "urn:wutsi:command:like-story"
    const val UNLIKE_STORY_COMMAND = "urn:wutsi:command:unlike-story"

    const val PIN_STORY_COMMAND = "urn:wutsi:command:pin-story"
    const val UNPIN_STORY_COMMAND = "urn:wutsi:command:unpin-story"

    // Event
    const val STORY_COMMENTED_EVENT = "urn:wutsi:event:story-commented"

    const val STORY_LIKED_EVENT = "urn:wutsi:event:story-liked"
    const val STORY_UNLIKED_EVENT = "urn:wutsi:event:story-unliked"

    const val STORY_PINED_EVENT = "urn:wutsi:event:story-pined"
    const val STORY_UNPINED_EVENT = "urn:wutsi:event:story-unpined"
}
