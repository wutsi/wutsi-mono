package com.wutsi.blog.transaction.dto

data class SearchSuperFanRequest(
    val walletId: String? = null,
    val userId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
