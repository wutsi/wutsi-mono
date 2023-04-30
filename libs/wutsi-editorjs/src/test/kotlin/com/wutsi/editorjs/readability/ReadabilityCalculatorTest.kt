package com.wutsi.editorjs.readability

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.editorjs.dom.EJSDocument
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class ReadabilityCalculatorTest {
    private lateinit var rule1: ReadabilityRule
    private lateinit var rule2: ReadabilityRule
    private lateinit var rule3: ReadabilityRule

    @Test
    fun evaluate() {
        val doc = EJSDocument()
        val ctx = ReadabilityContext()

        rule1 = mock()
        `when`(rule1.validate(doc, ctx)).thenReturn(createRuleResult(rule1, 100))

        rule2 = mock()
        `when`(rule2.validate(doc, ctx)).thenReturn(createRuleResult(rule2, 100))

        rule3 = mock()
        `when`(rule3.validate(doc, ctx)).thenReturn(createRuleResult(rule3, 50))

        val service = ReadabilityCalculator(arrayListOf(rule1, rule2, rule3))

        val result = service.compute(doc, ctx)

        assertEquals(83, result.score)
        assertEquals(3, result.ruleResults.size)
    }

    private fun createRuleResult(rule: ReadabilityRule, score: Int) = RuleResult(
        rule = rule,
        score = score,
    )
}
