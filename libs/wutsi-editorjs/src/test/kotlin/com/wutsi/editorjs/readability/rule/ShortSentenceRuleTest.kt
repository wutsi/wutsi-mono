package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShortSentenceRuleTest {
    val rule = ShortSentenceRule()
    val context = ReadabilityContext(
        maxWordsPerSentence = 3
    )

    @Test
    fun validate() {
        val doc = createDocument(
            arrayListOf(
                "Yo! This is another long sentence",
                "Allo world. This is a very long sentence",
                "para3",
                "para41. para42. para43"
            )
        )
        val result = rule.validate(doc, context)

        assertEquals(75, result.score)
    }

    @Test
    fun validate100() {
        val doc = createDocument(
            arrayListOf(
                "paragraph 1.",
                "paragraph 2. paragraph 21",
                "paragraph 3. paragraph 31",
                "paragraph 41. paragraph 42",
                "paragraph 51. paragraph 52",
                "paragraph 61"
            )
        )
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateEmpty() {
        val doc = createDocument(arrayListOf())
        val result = rule.validate(doc, context)

        assertEquals(0, result.score)
    }

    private fun createDocument(paragraphs: List<String>) = EJSDocument(
        blocks = paragraphs.map {
            Block(
                type = BlockType.paragraph,
                data = BlockData(text = it)
            )
        }.toMutableList()
    )
}
