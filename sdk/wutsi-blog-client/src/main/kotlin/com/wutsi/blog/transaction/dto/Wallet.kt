package com.wutsi.blog.transaction.dto

import java.util.Date

data class Wallet(
    val id: String = "",
    val userId: Long = -1,
    val country: String = "",
    val currency: String = "",
    var balance: Long = 0,
    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
)
