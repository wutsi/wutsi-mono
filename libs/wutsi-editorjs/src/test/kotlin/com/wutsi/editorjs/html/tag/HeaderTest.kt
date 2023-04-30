package com.wutsi.editorjs.html.tag

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HeaderTest {

    val tag = Header()

    @Test
    fun write() {
        val block = createBlock("Hello world", 2)
        val writer = StringWriter()

        tag.write(block, writer)

        assertEquals("<h2>Hello world</h2>\n", writer.toString())
    }

    @Test
    fun read() {
        val elt = createElement("h3", "Hello world")
        val block = tag.read(elt)

        assertEquals(BlockType.header, block.type)
        assertEquals("Hello world", block.data.text)
        assertEquals(3, block.data.level)
    }

    private fun createBlock(text: String, level: Int) = Block(
        type = BlockType.header,
        data = BlockData(
            text = text,
            level = level,
        ),
    )

    private fun createElement(tag: String, text: String): Element {
        val elt = Element(tag)
        elt.text(text)
        return elt
    }
}
