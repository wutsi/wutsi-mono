package com.wutsi.recommendation.domain

data class TermEntity(
    val text: String = "",
    val tf: Double? = null,
    val idf: Double? = null,
)
