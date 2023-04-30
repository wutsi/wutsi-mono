package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HeaderRuleTest {
    val rule = HeaderRule()
    val context = ReadabilityContext()

    @Test
    fun validateH1() {
        val doc = createDocument(1)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateH2() {
        val doc = createDocument(2)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateH3() {
        val doc = createDocument(3)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateH4() {
        val doc = createDocument(4)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateH5() {
        val doc = createDocument(5)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateH6() {
        val doc = createDocument(6)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateFailure() {
        val doc = createDocument()
        val result = rule.validate(doc, context)

        assertEquals(0, result.score)
    }

    private fun createDocument() = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hello world",
                ),
            ),
        ),
    )

    private fun createDocument(level: Int) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hello world",
                ),
            ),
            Block(
                type = BlockType.header,
                data = BlockData(
                    level = level,
                    text = "Hello world",
                ),
            ),
        ),
    )
}
