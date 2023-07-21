package com.wutsi.blog.nlp.service

data class Term(
    val text: String = "",
    val tf: Double? = null,
    val df: Double? = null,
)
