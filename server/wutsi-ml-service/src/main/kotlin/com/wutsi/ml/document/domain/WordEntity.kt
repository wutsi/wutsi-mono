package com.wutsi.ml.document.domain

data class WordEntity(
    val text: String = "",
    var tf: Double? = null,
    var tfIdf: Double? = null,
)
