package com.wutsi.blog.client.pin

import java.util.Date

data class PinDto(
    val id: Long = -1,
    val userId: Long = -1,
    val storyId: Long = -1,
    val creationDateTime: Date = Date(),
)
