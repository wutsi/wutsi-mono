package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MinImageRuleTest {
    val rule = MinImageRule()
    val context = ReadabilityContext()

    @Test
    fun validate() {
        val doc = createDocument()
        val result = rule.validate(doc, context)

        assertEquals(100, result.score)
    }

    @Test
    fun validateFailure() {
        val doc = createDocument()
        doc.blocks = doc.blocks.filter { it.type != BlockType.image }.toMutableList()
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
            Block(
                type = BlockType.image,
                data = BlockData(
                    url = "http://www.google.ca/1.png",
                ),
            ),
        ),
    )
}
