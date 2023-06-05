package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.Tag

data class SearchTagResponse(
    val tags: List<Tag> = emptyList(),
)
