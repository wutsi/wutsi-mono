package com.wutsi.blog.client.story

import javax.validation.constraints.NotEmpty

data class SortStoryRequest(
    @get:NotEmpty() val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
    val algorithm: SortAlgorithmType = SortAlgorithmType.most_recent,
)
