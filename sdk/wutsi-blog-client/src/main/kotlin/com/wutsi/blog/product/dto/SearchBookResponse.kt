package com.wutsi.blog.product.dto

data class SearchBookResponse(
    val books: List<BookSummary> = emptyList()
)
