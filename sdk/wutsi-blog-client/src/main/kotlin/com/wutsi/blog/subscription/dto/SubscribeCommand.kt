package com.wutsi.blog.subscription.dto

data class SubscribeCommand(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val email: String? = null,
    val storyId: Long? = null,
<<<<<<< Updated upstream
    val referer: String? = null,
=======
    val from: String? = null,
>>>>>>> Stashed changes
    val timestamp: Long = System.currentTimeMillis(),
)
