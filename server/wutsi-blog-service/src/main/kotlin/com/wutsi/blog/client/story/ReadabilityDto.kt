package com.wutsi.blog.client.story

data class ReadabilityDto(
    val score: Int = 0,
    val scoreThreshold: Int = 0,
    val rules: List<ReadabilityRuleDto> = emptyList(),
)
