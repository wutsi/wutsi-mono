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
        val block = createBlock("Hello <b>world</b>")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<p>Hello <b>world</b></p>\n", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement("yo <strong>man</strong>")
        val block = tag.read(elt)

        assertEquals(BlockType.paragraph, block.type)
        assertEquals("yo <strong>man</strong>", block.data.text)
    }

    private fun createBlock(text: String) = Block(
        type = BlockType.paragraph,
        data = BlockData(
            text = text
        )
    )

    private fun createElement(code: String): Element {
        val elt = Element("p")
        elt.html(code)
        return elt
    }
}
