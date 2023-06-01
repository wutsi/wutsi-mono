package com.wutsi.blog.event

object EventType {
    // Command
    const val COMMENT_STORY_COMMAND = "urn:wutsi:blog:command:comment-story"

    const val LIKE_STORY_COMMAND = "urn:wutsi:blog:command:like-story"
    const val UNLIKE_STORY_COMMAND = "urn:wutsi:blog:command:unlike-story"

    const val PIN_STORY_COMMAND = "urn:wutsi:blog:command:pin-story"
    const val UNPIN_STORY_COMMAND = "urn:wutsi:blog:command:unpin-story"

    const val SHARE_STORY_COMMAND = "urn:wutsi:blog:command:share-story"

    const val SUBSCRIBE_COMMAND = "urn:wutsi:blog:command:subscribe"
    const val UNSUBSCRIBE_COMMAND = "urn:wutsi:blog:command:unsubscribe"

    // Event
    const val STORY_COMMENTED_EVENT = "urn:wutsi:blog:event:story-commented"

    const val STORY_LIKED_EVENT = "urn:wutsi:blog:event:story-liked"
    const val STORY_UNLIKED_EVENT = "urn:wutsi:blog:event:story-unliked"

    const val STORY_PINED_EVENT = "urn:wutsi:blog:event:story-pined"
    const val STORY_UNPINED_EVENT = "urn:wutsi:blog:event:story-unpined"

    const val STORY_SHARED_EVENT = "urn:wutsi:blog:event:story-shared"

    const val SUBSCRIBED_EVENT = "urn:wutsi:blog:command:subscribed"
    const val UNSUBSCRIBED_EVENT = "urn:wutsi:blog:command:unsubscribed"
}
