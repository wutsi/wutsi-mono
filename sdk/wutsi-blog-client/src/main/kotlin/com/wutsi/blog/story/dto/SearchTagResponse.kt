package com.wutsi.blog.story.dto

data class SearchTagResponse(
    val tags: List<Tag> = emptyList(),
)
