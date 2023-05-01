package com.wutsi.blog.client.view

data class SearchPreferredAuthorRequest(
    val userId: Long? = null,
    val deviceId: String? = null,
)
