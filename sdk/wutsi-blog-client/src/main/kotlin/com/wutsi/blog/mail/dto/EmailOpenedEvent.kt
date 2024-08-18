package com.wutsi.blog.mail.dto

data class EmailOpenedEvent(
    val type: EmailType = EmailType.UNKNOWN,
    val userId: Long? = null,
    val storyId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
