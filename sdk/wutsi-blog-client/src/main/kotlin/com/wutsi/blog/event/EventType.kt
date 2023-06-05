package com.wutsi.blog.event

object EventType {
    // Command
    const val CREATE_BLOG_COMMAND = "urn:wutsi:blog:command:create-blog"

    const val COMMENT_STORY_COMMAND = "urn:wutsi:blog:command:comment-story"

    const val LIKE_STORY_COMMAND = "urn:wutsi:blog:command:like-story"
    const val UNLIKE_STORY_COMMAND = "urn:wutsi:blog:command:unlike-story"

    const val LOGIN_USER_COMMAND = "urn:wutsi:blog:command:login-user"
    const val LOGOUT_USER_COMMAND = "urn:wutsi:blog:command:logout-user"

    const val PIN_STORY_COMMAND = "urn:wutsi:blog:command:pin-story"
    const val UNPIN_STORY_COMMAND = "urn:wutsi:blog:command:unpin-story"

    const val SHARE_STORY_COMMAND = "urn:wutsi:blog:command:share-story"

    const val SUBSCRIBE_COMMAND = "urn:wutsi:blog:command:subscribe"
    const val UNSUBSCRIBE_COMMAND = "urn:wutsi:blog:command:unsubscribe"

    const val UPDATE_USER_ATTRIBUTE_COMMAND = "urn:wutsi:blog:command:update-user-attribute"

    // Event
    const val BLOG_CREATED_EVENT = "urn:wutsi:blog:event:blog-created"

    const val STORY_COMMENTED_EVENT = "urn:wutsi:blog:event:story-commented"
    const val STORY_LIKED_EVENT = "urn:wutsi:blog:event:story-liked"
    const val STORY_IMPORTED_EVENT = "urn:wutsi:blog:event:story-imported"
    const val STORY_IMPORT_FAILED_EVENT = "urn:wutsi:blog:event:story-import-failed"
    const val STORY_PUBLISHED_EVENT = "urn:wutsi:blog:event:story-published"
    const val STORY_PUBLICATION_SCHEDULED_EVENT = "urn:wutsi:blog:event:story-publication-scheduled"
    const val STORY_PINED_EVENT = "urn:wutsi:blog:event:story-pined"
    const val STORY_SHARED_EVENT = "urn:wutsi:blog:event:story-shared"
    const val STORY_UNLIKED_EVENT = "urn:wutsi:blog:event:story-unliked"
    const val STORY_UNPINED_EVENT = "urn:wutsi:blog:event:story-unpined"

    const val USER_ATTRIBUTE_UPDATED_EVENT = "urn:wutsi:blog:command:user-attribute-updated"
    const val USER_LOGGED_IN_EVENT = "urn:wutsi:blog:event:user-logged-in"
    const val USER_LOGGED_OUT_EVENT = "urn:wutsi:blog:event:user-logged-out"

    const val SUBSCRIBED_EVENT = "urn:wutsi:blog:event:subscribed"
    const val UNSUBSCRIBED_EVENT = "urn:wutsi:blog:event:unsubscribed"
}
