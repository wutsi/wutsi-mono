package com.wutsi.blog.pin.dto

data class SearchPinRequest(
    val userIds: List<Long> = emptyList(),
)
