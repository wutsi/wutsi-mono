package com.wutsi.blog.story.dto

data class SearchReaderResponse(
    val readers: List<Reader> = emptyList(),
)
