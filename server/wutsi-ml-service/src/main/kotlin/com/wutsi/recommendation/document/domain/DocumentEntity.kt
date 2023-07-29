package com.wutsi.recommendation.document.domain

data class DocumentEntity(
    val id: Long,
    val content: String,
    val language: String,
)
