package com.wutsi.recommendation.domain

data class WordEntity(
    val text: String = "",
    var tf: Double? = null,
    var tfIdf: Double? = null,
)
