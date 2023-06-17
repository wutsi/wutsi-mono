package com.wutsi.blog.story.dto

import com.wutsi.blog.user.dto.Readability

data class GetStoryReadabilityResponse(
    val readability: Readability = Readability(),
)
