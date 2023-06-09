package com.wutsi.blog.event

object EventType {
    // Command
    const val COMMENT_STORY_COMMAND = "urn:wutsi:blog:command:comment-story"

    const val LIKE_STORY_COMMAND = "urn:wutsi:blog:command:like-story"
    const val UNLIKE_STORY_COMMAND = "urn:wutsi:blog:command:unlike-story"

    const val LOGIN_USER_COMMAND = "urn:wutsi:blog:command:login-user"
    const val LOGOUT_USER_COMMAND = "urn:wutsi:blog:command:logout-user"

    const val PIN_STORY_COMMAND = "urn:wutsi:blog:command:pin-story"
    const val UNPIN_STORY_COMMAND = "urn:wutsi:blog:command:unpin-story"

    const val SEND_STORY_DAILY_EMAIL_COMMAND = "urn:wutsi:blog:event:send-daily-email"

    const val SHARE_STORY_COMMAND = "urn:wutsi:blog:command:share-story"
    const val VIEW_STORY_COMMAND = "urn:wutsi:blog:command:view-story"

    const val SUBSCRIBE_COMMAND = "urn:wutsi:blog:command:subscribe"
    const val UNSUBSCRIBE_COMMAND = "urn:wutsi:blog:command:unsubscribe"

    const val SUBMIT_DONATION_COMMAND = "urn:wutsi:blog:command:submit-donation"
    const val SUBMIT_CASHOUT_COMMAND = "urn:wutsi:blog:command:submit-cashout"
    const val SUBMIT_TRANSACTION_NOTIFICATION_COMMAND = "urn:wutsi:blog:command:submit-transaction-notification"

    // Event
    const val BLOG_CREATED_EVENT = "urn:wutsi:blog:event:blog-created"

    const val STORY_COMMENTED_EVENT = "urn:wutsi:blog:event:story-commented"
    const val STORY_CREATED_EVENT = "urn:wutsi:blog:event:story-created"
    const val STORY_DELETED_EVENT = "urn:wutsi:blog:event:story-deleted"
    const val STORY_LIKED_EVENT = "urn:wutsi:blog:event:story-liked"
    const val STORY_IMPORTED_EVENT = "urn:wutsi:blog:event:story-imported"
    const val STORY_IMPORT_FAILED_EVENT = "urn:wutsi:blog:event:story-import-failed"
    const val STORY_PUBLISHED_EVENT = "urn:wutsi:blog:event:story-published"
    const val STORY_PUBLICATION_SCHEDULED_EVENT = "urn:wutsi:blog:event:story-publication-scheduled"
    const val STORY_PINED_EVENT = "urn:wutsi:blog:event:story-pined"
    const val STORY_SHARED_EVENT = "urn:wutsi:blog:event:story-shared"
    const val STORY_UNLIKED_EVENT = "urn:wutsi:blog:event:story-unliked"
    const val STORY_UNPINED_EVENT = "urn:wutsi:blog:event:story-unpined"
    const val STORY_UNPUBLISHED_EVENT = "urn:wutsi:blog:event:story-unpublished"
    const val STORY_UPDATED_EVENT = "urn:wutsi:blog:event:story-updated"
    const val STORY_DAILY_EMAIL_SENT_EVENT = "urn:wutsi:blog:event:story-daily-email-sent"

    const val USER_ATTRIBUTE_UPDATED_EVENT = "urn:wutsi:blog:command:user-attribute-updated"
    const val USER_ACTIVATED_EVENT = "urn:wutsi:blog:command:user-activated"
    const val USER_DEACTIVATED_EVENT = "urn:wutsi:blog:command:user-deactivated"
    const val USER_LOGGED_IN_EVENT = "urn:wutsi:blog:event:user-logged-in"
    const val USER_LOGGED_IN_AS_EVENT = "urn:wutsi:blog:event:user-logged-in-as"
    const val USER_LOGGED_OUT_EVENT = "urn:wutsi:blog:event:user-logged-out"

    const val SUBSCRIBED_EVENT = "urn:wutsi:blog:event:subscribed"
    const val UNSUBSCRIBED_EVENT = "urn:wutsi:blog:event:unsubscribed"

    const val TRANSACTION_SUBMITTED_EVENT = "urn:wutsi:blog:event:transaction-submitted"
    const val TRANSACTION_FAILED_EVENT = "urn:wutsi:blog:event:transaction-failed"
    const val TRANSACTION_SUCCEEDED_EVENT = "urn:wutsi:blog:event:transaction-succeeded"
    const val TRANSACTION_NOTIFICATION_SUBMITTED_EVENT = "urn:wutsi:blog:event:transaction-notification-submitted"

    const val WALLET_CREATED_EVENT = "urn:wutsi:blog:event:wallet-created"
    const val WALLET_ACCOUNT_UPDATED_EVENT = "urn:wutsi:blog:event:wallet-account-updated"
}
