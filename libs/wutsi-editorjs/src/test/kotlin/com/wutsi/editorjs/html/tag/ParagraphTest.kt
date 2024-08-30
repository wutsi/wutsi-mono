package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class ParagraphTest {
    val tag = Paragraph()

    @Test
    fun export() {
        val block = createBlock("Hello <b>world</b>", true)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<p class='centered'>Hello <b>world</b></p>\n", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement("yo <strong>man</strong>")
        val block = tag.read(elt)

        assertEquals(BlockType.paragraph, block.type)
        assertEquals("yo <strong>man</strong>", block.data.text)
    }

    private fun createBlock(text: String, centered: Boolean = false) = Block(
        type = BlockType.paragraph,
        data = BlockData(
            text = text,
            centered = centered,
        ),
    )

    private fun createElement(code: String): Element {
        val elt = Element("p")
        elt.html(code)
        return elt
    }
}
