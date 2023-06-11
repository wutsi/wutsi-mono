package com.wutsi.blog.subscription.dto

import com.wutsi.blog.user.dto.Readability

data class GetStoryReadabilityResponse(
    val readability: Readability = Readability(),
)
