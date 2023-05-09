package com.wutsi.blog.client.view

import java.util.Date

data class ViewDto(
    val id: Long = -1,
    val userId: Long? = null,
    val storyId: Long = -1,
    val deviceId: String = "",
    val hitId: String = "",
    val source: String? = null,
    val campaign: String? = null,
    val medium: String? = null,
    val referer: String? = null,
    var viewDateTime: Date = Date(),
)
