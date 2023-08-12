package com.wutsi.ml.personalize.dto

data class SortStoryRequest(
    val userId: Long,
    val storyIds: List<Long> = emptyList(),
)
