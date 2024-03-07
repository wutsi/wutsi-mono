package com.wutsi.blog.app.form

data class TrackForm(
    val time: Long,
    val event: String,
    val ua: String,
    val value: String?,
    val hitId: String,
    val url: String,
    val referrer: String?,
    val campaign: String? = null,
    val storyId: String? = null,
    val page: String? = null,
    val businessId: String? = null,
)
