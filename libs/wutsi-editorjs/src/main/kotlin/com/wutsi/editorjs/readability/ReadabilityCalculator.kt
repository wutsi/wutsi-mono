package com.wutsi.editorjs.readability

import com.wutsi.editorjs.dom.EJSDocument

class ReadabilityCalculator(
    private val rules: List<ReadabilityRule>,
) {
    companion object {
        const val MAX_SCORE = 100
    }

    fun compute(doc: EJSDocument, context: ReadabilityContext): ReadabilityResult {
        val ruleResults = rules.map { it.validate(doc, context) }
        return ReadabilityResult(
            score = computeScopre(ruleResults),
            ruleResults = ruleResults,
        )
    }

    private fun computeScopre(ruleResults: List<RuleResult>): Int {
        val ruleCount = rules.size
        val score = ruleResults.sumOf { it.score }
        return score / ruleCount
    }
}
