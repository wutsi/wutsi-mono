package com.wutsi.blog.endorsement.dto

data class SearchEndorsementRequest(
    val userIds: List<Long> = emptyList(),
    val endorserId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
