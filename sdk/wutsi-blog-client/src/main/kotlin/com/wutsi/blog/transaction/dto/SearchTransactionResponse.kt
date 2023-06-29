package com.wutsi.blog.transaction.dto

data class SearchTransactionResponse(
    val transactions: List<TransactionSummary> = emptyList(),
)
