package com.wutsi.ml.document.domain

data class DocumentEntity(
    val id: Long,
    val authorId: Long,
    val content: String,
    val language: String,
)
