package com.wutsi.blog.transaction.dto

data class GetTransactionResponse(
    val transaction: Transaction = Transaction(),
)
