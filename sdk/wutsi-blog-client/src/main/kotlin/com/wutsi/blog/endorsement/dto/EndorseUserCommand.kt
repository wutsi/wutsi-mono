package com.wutsi.blog.endorsement.dto

data class EndorseUserCommand(
    val userId: Long = -1,
    val endorserId: Long = -1,
    val blurb: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
