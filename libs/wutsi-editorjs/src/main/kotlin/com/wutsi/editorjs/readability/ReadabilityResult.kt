package com.wutsi.editorjs.readability

data class ReadabilityResult (
        val score: Int,
        val ruleResults: List<RuleResult>
)
