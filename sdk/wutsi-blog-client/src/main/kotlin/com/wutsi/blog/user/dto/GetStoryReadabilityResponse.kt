package com.wutsi.blog.user.dto

data class GetStoryReadabilityResponse(
    val readability: Readability = Readability(),
)
