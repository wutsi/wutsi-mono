package com.wutsi.blog.user.dto

data class Readability(
    val score: Int = 0,
    val scoreThreshold: Int = 0,
    val rules: List<ReadabilityRule> = emptyList(),
)
