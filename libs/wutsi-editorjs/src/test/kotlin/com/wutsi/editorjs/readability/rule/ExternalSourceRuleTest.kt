package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.ListStyle
import com.wutsi.editorjs.readability.ReadabilityContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExternalSourceRuleTest {
    val rule = ExternalSourceRule()
    val context = ReadabilityContext()

    @Test
    fun validateListWithLink() {
        val doc = createDocumentWithBullet(ListStyle.ordered, listOf(
            "Hello <b>world</b>",
            "How are <a href='http://www.google.com'>You</a>"
        ))
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateListWithoutLinks() {
        val doc = createDocumentWithBullet(ListStyle.ordered, listOf(
            "Hello <b>world</b>",
            "How are <i>You</i>"
        ))
        val result = rule.validate(doc, context)

        assertEquals(0, result.score)
    }

    @Test
    fun validateParagraphWithLink() {
        val doc = createDocumentWithParagraph("How are <a href='http://www.google.com'>You</a>")
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateParagraphWithoutLinks() {
        val doc = createDocumentWithParagraph("How are you...")
        val result = rule.validate(doc, context)

        assertEquals(0, result.score)
    }

    @Test
    fun validateQuote() {
        val doc = createDocumentWithQuote("How are you...")
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateLink() {
        val doc = createDocumentWithLink("How are you...")
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateEmbed() {
        val doc = createDocumentWithEmbed("How are you...")
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }


    private fun createDocumentWithBullet(style: ListStyle, items: List<String>) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.list,
                data = BlockData(
                    style = style,
                    items = items
                )
            )
        )
    )

    private fun createDocumentWithParagraph(text: String) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = text
                )
            )
        )
    )

    private fun createDocumentWithQuote(text: String) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.quote,
                data = BlockData(
                    text = text
                )
            )
        )
    )

    private fun createDocumentWithLink(text: String) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.linkTool,
                data = BlockData(
                    text = text
                )
            )
        )
    )

    private fun createDocumentWithEmbed(text: String) = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.embed,
                data = BlockData(
                    text = text
                )
            )
        )
    )
}
