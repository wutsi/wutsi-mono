package com.wutsi.ml.personalize.dto

data class RecommendStoryRequest(
    val userId: Long = -1,
    val limit: Int = 1000,
)
