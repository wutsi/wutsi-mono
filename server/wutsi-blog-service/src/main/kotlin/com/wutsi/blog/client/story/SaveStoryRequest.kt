package com.wutsi.blog.client.story

import javax.validation.constraints.NotNull

data class SaveStoryRequest(
    @get:NotNull val siteId: Long? = null,
    @get:NotNull val accessToken: String? = null,
    @get:NotNull val contentType: String? = null,
    val title: String? = null,
    val content: String? = null,
    val sourceUrl: String? = null,
    val sourceSite: String? = null,
)
