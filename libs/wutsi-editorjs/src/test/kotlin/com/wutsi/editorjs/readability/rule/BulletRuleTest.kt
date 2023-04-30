package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.ListStyle
import com.wutsi.editorjs.readability.ReadabilityContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BulletRuleTest {
    val rule = BulletRule()
    val context = ReadabilityContext()

    @Test
    fun validateOL() {
        val doc = createDocument(ListStyle.ordered)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateUL() {
        val doc = createDocument(ListStyle.unordered)
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateEmptyList() {
        val doc = createDocument(ListStyle.unordered, true)
        val result = rule.validate(doc, context)

        assertEquals(0, result.score)
    }

    @Test
    fun validateNoList() {
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

    private fun createDocument(style: ListStyle, empty: Boolean = false) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hello world",
                ),
            ),
            Block(
                type = BlockType.list,
                data = BlockData(
                    style = style,
                    items = if (empty) arrayListOf() else arrayListOf("item1", "item2", "item3"),
                ),
            ),
        ),
    )
}
