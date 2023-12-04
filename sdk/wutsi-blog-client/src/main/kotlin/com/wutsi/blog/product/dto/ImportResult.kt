package com.wutsi.blog.product.dto

data class ImportResult(
    val url: String = "",
    val externalIds: List<String> = emptyList(),
    val importedCount: Int = 0,
    val unpublishedCount: Int = 0,
    val errors: List<ImportError> = emptyList(),
)
