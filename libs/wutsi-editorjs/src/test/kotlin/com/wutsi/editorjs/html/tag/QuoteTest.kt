package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class QuoteTest {

    val tag = Quote()

    @Test
    fun write() {
        val block = createBlock("Hello", "world")
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<blockquote><p>Hello</p><footer>world</footer></blockquote>\n", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement("yo", "man")
        val block = tag.read(elt)

        assertEquals(BlockType.quote, block.type)
        assertEquals("yo", block.data.text)
        assertEquals("man", block.data.caption)
    }

    @Test
    fun readNoFooter() {
        val elt = createElement("yo", "")
        val block = tag.read(elt)

        assertEquals(BlockType.quote, block.type)
        assertEquals("yo", block.data.text)
        assertEquals("", block.data.caption)
    }

    private fun createBlock(text: String, caption: String) = Block(
        type = BlockType.code,
        data = BlockData(
            text = text,
            caption = caption,
        ),
    )

    private fun createElement(text: String, caption: String): Element {
        val elt = Element("blockquote")
        if (text.isNotEmpty()) {
            elt.appendChild(Element("p").text(text))
        }
        if (caption.isNotEmpty()) {
            elt.appendChild(Element("footer").text(caption))
        }
        return elt
    }
}
