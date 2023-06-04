package com.wutsi.blog.client.story

import javax.validation.constraints.NotNull

@Deprecated("")
data class ImportStoryRequest(
    @get:NotNull val siteId: Long? = null,
    @get:NotNull val accessToken: String? = null,
    @get:NotNull val url: String? = null,
)
