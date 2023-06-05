package com.wutsi.blog.app.model

data class ReadabilityModel(
    val score: Int = 0,
    val scoreThreshold: Int = 0,
    val color: String = "",
    val rules: List<ReadabilityRuleModel> = emptyList(),
)
