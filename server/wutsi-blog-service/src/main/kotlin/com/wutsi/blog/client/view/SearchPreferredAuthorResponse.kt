package com.wutsi.blog.client.view

data class SearchPreferredAuthorResponse(
    val authors: List<PreferredAuthorDto> = emptyList(),
)
