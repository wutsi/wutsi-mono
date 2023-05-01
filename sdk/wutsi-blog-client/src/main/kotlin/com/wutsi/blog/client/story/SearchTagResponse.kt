package com.wutsi.blog.client.story

data class SearchTagResponse(
    val tags: List<TagDto> = emptyList(),
)
