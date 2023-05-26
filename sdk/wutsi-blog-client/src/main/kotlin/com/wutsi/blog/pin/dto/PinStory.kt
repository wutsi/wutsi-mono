package com.wutsi.blog.pin.dto

import java.util.Date

class PinStory(
    val userId: Long = -1,
    var storyId: Long = -1,
    var timestamp: Date = Date(),
)
