package com.wutsi.blog.error

object ErrorCode {
    val USER_SUSPENDED: String = "urn:wutsi:blog:error:user-suspended"
    val USER_NOT_FOUND: String = "urn:wutsi:blog:error:user-not-found"
    val USER_NAME_DUPLICATE: String = "urn:wutsi:blog:error:user-name-duplicate"
    val USER_EMAIL_DUPLICATE: String = "urn:wutsi:blog:error:user-email-duplicate"

    val SESSION_EXPIRED: String = "urn:wutsi:blog:error:session-expired"
    val SESSION_NOT_FOUND: String = "urn:wutsi:blog:error:session-not-found"

    val STORY_NOT_FOUND: String = "urn:wutsi:blog:error:story-not-found"
    val STORY_ALREADY_IMPORTED: String = "urn:wutsi:blog:error:story-already-imported"
    val STORY_IMPORT_FAILED: String = "urn:wutsi:blog:error:story-import-failed"
    val STORY_WITHOUT_CONTENT: String = "urn:wutsi:blog:error:story-without-content"

    val PERMISSION_DENIED: String = "urn:wutsi:blog:error:permission-denied"
}
